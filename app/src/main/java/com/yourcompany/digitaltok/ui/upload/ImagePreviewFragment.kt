package com.yourcompany.digitaltok.ui.upload

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.databinding.FragmentImagePreviewBinding
import com.yourcompany.digitaltok.ui.faq.HelpFragment
import java.io.IOException
import kotlin.math.ceil

class ImagePreviewFragment : Fragment(), NfcAdapter.ReaderCallback {

    private var _binding: FragmentImagePreviewBinding? = null
    private val binding get() = _binding!!

    private val imageViewModel: ImageViewModel by viewModels()

    private var loadingDialog: AlertDialog? = null
    private var nfcTransferDialog: AlertDialog? = null
    private var resultDialog: AlertDialog? = null

    private var nfcAdapter: NfcAdapter? = null
    private var binaryDataToWrite: ByteArray? = null

    @Volatile
    private var isTransferring = false

    @Volatile
    private var transferCompleted = false

    // 다이얼로그 내 상태 텍스트(레이아웃에 없으면 null일 수 있음)
    private var nfcStatusTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageId = arguments?.getInt(ARG_IMAGE_ID)
        val previewUrl = arguments?.getString(ARG_PREVIEW_URL)

        setupToolbar()
        setupClickListeners(imageId)
        observeViewModel()

        if (previewUrl != null) {
            Glide.with(this)
                .load(previewUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivPreview)
        } else {
            showFailDialog("이미지 정보를 불러오는데 실패했습니다.")
        }
    }

    override fun onResume() {
        super.onResume()
        // 20초+ 태깅이면 화면 꺼짐 방지 필수
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        // 전송 중에는 readerMode/dismiss 하지 않음 (끊기면 실패)
        if (!isTransferring) {
            disableNfcReaderMode()
            nfcTransferDialog?.dismiss()
        }
        resultDialog?.dismiss()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    private fun setupToolbar() {
        binding.appBar.titleTextView.text = "사진 미리보기"
        binding.appBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupClickListeners(imageId: Int?) {
        binding.btnSendToDiring.setOnClickListener {
            if (nfcAdapter == null) {
                showFailDialog("이 기기는 NFC를 지원하지 않습니다.")
                return@setOnClickListener
            }

            if (!nfcAdapter!!.isEnabled) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("NFC 비활성화")
                    .setMessage("NFC 기능이 꺼져있습니다. 이미지 전송을 위해 NFC를 활성화해주세요.")
                    .setPositiveButton("설정으로 이동") { _, _ ->
                        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                    }
                    .setNegativeButton("취소", null)
                    .show()
                return@setOnClickListener
            }

            if (imageId != null) {
                showLoadingDialog("바이너리 데이터 준비 중...")
                imageViewModel.fetchAndDownloadBinary(imageId)
            } else {
                showFailDialog("전송할 이미지 정보가 없습니다.")
            }
        }
    }

    private fun observeViewModel() {
        imageViewModel.binaryData.observe(viewLifecycleOwner) { result ->
            hideLoadingDialog()
            result.onSuccess { data ->
                binaryDataToWrite = data
                transferCompleted = false
                showNfcTransferDialog()
            }.onFailure { e ->
                showFailDialog("오류: ${e.message}")
            }
        }
    }

    // ======================================================
    // NFC ReaderCallback (IsoDep 전용)
    // ======================================================
    override fun onTagDiscovered(tag: Tag?) {
        if (tag == null) return

        // 전송 완료 후엔 추가 콜백 무시
        if (transferCompleted) return

        // 전송 중 중복 콜백 무시
        if (isTransferring) {
            Log.d(TAG, "Already transferring, ignore")
            return
        }
        isTransferring = true

        val data = binaryDataToWrite
        if (data == null) {
            showFailDialog("전송할 데이터가 준비되지 않았습니다.")
            isTransferring = false
            return
        }

        val isoDep = IsoDep.get(tag)
        if (isoDep == null) {
            showFailDialog("IsoDep 태그가 아닙니다.")
            isTransferring = false
            return
        }

        try {
            updateNfcDialogText("태그 인식됨. 전송을 시작합니다… (떼지 마세요)")
            isoDep.timeout = 120_000
            if (!isoDep.isConnected) isoDep.connect()

            // 0) SELECT (제조사 예시)
            val selectResp = transceiveHex(isoDep, "00A4040007D2760000850101")
            Log.d(TAG, "SELECT <- ${bytesToHex(selectResp)}")
            if (!endsWith9000(selectResp)) {
                showFailDialog("기기 선택 실패: ${bytesToHex(selectResp)}")
                return
            }

            // 2) 전송 (F0D2)
            updateNfcDialogText("이미지 전송 중… (떼지 마세요)")
            val screenIndex = 0
            val ok = writeBinaryByChunks(isoDep, data, screenIndex)
            if (!ok) return

            // 3) Refresh 요청 + 완료될 때까지 Poll (F0D4 + F0DE)
            updateNfcDialogText("디바이스에서 이미지 적용 중… (최대 수십 초 걸릴 수 있어요)")
            val done = refreshScreenAndWait(isoDep, screenIndex)
            if (!done) {
                showFailDialog("전송은 되었지만 갱신이 완료되지 않았습니다. 다시 시도해주세요.")
                return
            }

            // 4) 성공
            transferCompleted = true
            showSuccessDialogAndExit()
        } catch (e: IOException) {
            Log.e(TAG, "IsoDep IO 실패(태그 떨어짐 가능)", e)
            showFailDialog("태그가 떨어졌어요. 다시 대고 시도해 주세요.")
        } catch (e: Exception) {
            Log.e(TAG, "IsoDep 전송 실패", e)
            showFailDialog("전송 실패: ${e.message}")
        } finally {
            isTransferring = false
            try {
                isoDep.close()
            } catch (e: Exception) {
                Log.e(TAG, "IsoDep close 실패", e)
            }
        }
    }

    /**
     * 데이터 전송: APDU = F0 D2 [screenIndex] [rowIndex] FA [250 bytes]
     */
    private fun writeBinaryByChunks(isoDep: IsoDep, data: ByteArray, screenIndex: Int): Boolean {
        val chunkSize = 0xFA // 250
        val rowCount = ceil(data.size / chunkSize.toDouble()).toInt()
        Log.d(TAG, "payloadBytes=${data.size}, rowCount=$rowCount")

        val padded = if (data.size % chunkSize == 0) {
            data
        } else {
            ByteArray(rowCount * chunkSize).also { out ->
                System.arraycopy(data, 0, out, 0, data.size)
            }
        }

        val apdu = ByteArray(chunkSize + 5)
        apdu[0] = 0xF0.toByte()
        apdu[1] = 0xD2.toByte()
        apdu[2] = screenIndex.toByte()
        apdu[4] = chunkSize.toByte()

        for (rowIndex in 0 until rowCount) {
            apdu[3] = rowIndex.toByte()

            val start = rowIndex * chunkSize
            for (j in 0 until chunkSize) {
                apdu[5 + j] = padded[start + j]
            }

            val resp = isoDep.transceive(apdu)
            val respHex = bytesToHex(resp)
            Log.d(TAG, "D2 row=$rowIndex <- $respHex")

            if (!endsWith9000(resp)) {
                showFailDialog("전송 오류(row=$rowIndex): $respHex")
                return false
            }

            // 진행 표시(대략)
            if (rowCount >= 10 && rowIndex % (rowCount / 10).coerceAtLeast(1) == 0) {
                val percent = ((rowIndex + 1) * 100) / rowCount
                updateNfcDialogText("이미지 전송 중… $percent% (떼지 마세요)")
            }
        }

        return true
    }

    private fun refreshScreenAndWait(isoDep: IsoDep, screenIndex: Int): Boolean {
        val refresh = byteArrayOf(
            0xF0.toByte(),
            0xD4.toByte(),
            0x05.toByte(),
            (screenIndex or 0x80).toByte(),
            0x00.toByte()
        )

        val first = isoDep.transceive(refresh)
        val firstHex = bytesToHex(first)
        Log.d(TAG, "REFRESH <- $firstHex")

        return pollRefreshResult(isoDep)
    }

    private fun pollRefreshResult(isoDep: IsoDep): Boolean {
        val poll = byteArrayOf(
            0xF0.toByte(),
            0xDE.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x01.toByte()
        )

        repeat(1000) { i ->
            val resp = isoDep.transceive(poll)
            val hex = bytesToHex(resp)
            Log.d(TAG, "POLL[$i] <- $hex")

            when (hex) {
                "009000", "9000" -> return true
                "019000" -> {
                    if (i % 10 == 0) {
                        val seconds = (i / 10)
                        updateNfcDialogText("디바이스 적용 중… ${seconds}s (떼지 마세요)")
                    }
                    Thread.sleep(100)
                }
                "698A", "6986", "68C6" -> return false
                else -> return false
            }
        }
        return false
    }

    // ======================================================
    // Dialog + ReaderMode
    // ======================================================
    private fun showNfcTransferDialog() {
        if (nfcTransferDialog == null) {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nfc_transfer, null)

            // 레이아웃에 상태 텍스트뷰가 있으면 연결 (id: tv_status)
            nfcStatusTextView = dialogView.findViewById(
                resources.getIdentifier("tv_status", "id", requireContext().packageName)
            )

            nfcTransferDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .setOnDismissListener {
                    if (!isTransferring) disableNfcReaderMode()
                }
                .create()

            nfcTransferDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        updateNfcDialogText("NFC 태그에 휴대폰을 가까이 대주세요")
        nfcTransferDialog?.show()
        enableNfcReaderMode()
    }

    private fun updateNfcDialogText(text: String) {
        activity?.runOnUiThread {
            nfcStatusTextView?.text = text
        }
    }

    private fun enableNfcReaderMode() {
        val activity = requireActivity()
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

        nfcAdapter?.enableReaderMode(activity, this, flags, null)
    }

    private fun disableNfcReaderMode() {
        try {
            val activity = requireActivity()
            nfcAdapter?.disableReaderMode(activity)
        } catch (e: Exception) {
            Log.w(TAG, "Error disabling reader mode", e)
        }
    }

    // ======================================================
    // Success / Fail Dialog (custom xml)
    // ======================================================
    private fun showSuccessDialogAndExit() {
        activity?.runOnUiThread {
            nfcTransferDialog?.dismiss()
            if (resultDialog?.isShowing == true) return@runOnUiThread

            val dialogView = layoutInflater.inflate(R.layout.dialog_transfer_success, null)

            resultDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()
                .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }

            resultDialog?.show()

            binding.root.postDelayed({
                if (!isAdded) return@postDelayed
                resultDialog?.dismiss()
                disableNfcReaderMode()
                parentFragmentManager.popBackStack()
            }, 2000)
        }
    }

    private fun showFailDialog(message: String?) {
        activity?.runOnUiThread {
            nfcTransferDialog?.dismiss()
            if (resultDialog?.isShowing == true) return@runOnUiThread

            Log.e(TAG, "Transfer failed: $message")

            val dialogView = layoutInflater.inflate(R.layout.dialog_transfer_fail, null)
            val btnRetry = dialogView.findViewById<MaterialButton>(R.id.btnRetry)
            val btnSupport = dialogView.findViewById<MaterialButton>(R.id.btnSupport)

            resultDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()
                .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }

            btnRetry.setOnClickListener {
                resultDialog?.dismiss()
                transferCompleted = false
                showNfcTransferDialog()
            }

            btnSupport.setOnClickListener {
                resultDialog?.dismiss()
                if (!isAdded) return@setOnClickListener

                val containerId = (requireView().parent as ViewGroup).id
                if (containerId != View.NO_ID) {
                    parentFragmentManager.beginTransaction()
                        .replace(containerId, HelpFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }

            resultDialog?.show()
        }
    }

    // ======================================================
    // Loading dialog
    // ======================================================
    private fun showLoadingDialog(message: String) {
        if (loadingDialog == null) {
            val progressBar = ProgressBar(requireContext()).apply {
                val padding = 100
                setPadding(padding, padding, padding, padding)
            }
            loadingDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(message)
                .setView(progressBar)
                .setCancelable(false)
                .create()
        } else {
            loadingDialog?.setTitle(message)
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        nfcTransferDialog = null
        loadingDialog = null
        resultDialog = null
        nfcStatusTextView = null
    }

    // ======================================================
    // Utils
    // ======================================================
    private fun transceiveHex(isoDep: IsoDep, hex: String): ByteArray {
        val req = hexToBytes(hex)
        Log.d(TAG, "-> $hex")
        return isoDep.transceive(req)
    }

    private fun endsWith9000(resp: ByteArray): Boolean {
        if (resp.size < 2) return false
        val sw1 = resp[resp.size - 2].toInt() and 0xFF
        val sw2 = resp[resp.size - 1].toInt() and 0xFF
        return sw1 == 0x90 && sw2 == 0x00
    }

    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace(" ", "").trim()
        require(clean.length % 2 == 0) { "Invalid hex length" }
        val out = ByteArray(clean.length / 2)
        for (i in out.indices) {
            val idx = i * 2
            out[i] = clean.substring(idx, idx + 2).toInt(16).toByte()
        }
        return out
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) sb.append(String.format("%02X", b))
        return sb.toString()
    }

    companion object {
        private const val TAG = "ImagePreviewFragment"
        private const val ARG_IMAGE_ID = "IMAGE_ID"
        private const val ARG_PREVIEW_URL = "PREVIEW_URL"

        fun newInstance(imageId: Int, previewUrl: String) = ImagePreviewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_IMAGE_ID, imageId)
                putString(ARG_PREVIEW_URL, previewUrl)
            }
        }
    }
}

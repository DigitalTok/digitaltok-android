package com.yourcompany.digitaltok.ui.device

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceSearchingBinding

class DeviceSearchingFragment : Fragment() {

    private var _binding: FragmentDeviceSearchingBinding? = null
    private val binding get() = _binding!!

    // 서버 통신을 위한 ViewModel
    private val deviceViewModel: DeviceViewModel by viewModels()
    // MainActivity와 NFC 태그 정보를 공유하기 위한 ViewModel
    private val nfcViewModel: NfcViewModel by activityViewModels()

    // NFC 관련 객체
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceSearchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNfc()
        setupViews()
        observeViewModels()
    }

    override fun onResume() {
        super.onResume()
        // 이 화면이 활성화될 때, NFC 포그라운드 디스패치 활성화
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        // 이 화면이 비활성화될 때, NFC 포그라운드 디스패치 비활성화
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "이 기기는 NFC를 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // NFC 태그가 감지되면 MainActivity로 인텐트를 보냄
        val intent = Intent(requireContext(), requireActivity().javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    private fun setupViews() {
        binding.searchingTopAppbar.titleTextView.text = "장치 (Device)"
        binding.searchingTopAppbar.backButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun observeViewModels() {
        // 1. MainActivity가 전달한 NFC 태그를 관찰
        nfcViewModel.tag.observe(viewLifecycleOwner) { tag ->
            if (tag != null) {
                val uid = bytesToHexString(tag.id)
                Log.d("NFC", "Tag detected with UID: $uid")
                // 2. 태그가 감지되면, ViewModel을 통해 서버에 등록 요청
                deviceViewModel.registerDevice(uid)

                // 3. 태그 처리가 완료되었음을 알림 (중복 처리 방지)
                nfcViewModel.tagHandled()
            }
        }

        // 4. 서버 등록 결과를 관찰
        deviceViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d("NFC", "Registration Success: $it")
                // 5. 성공 -> 성공 화면으로 전환
                parentFragmentManager.beginTransaction()
                    .replace((requireView().parent as ViewGroup).id, DeviceSuccessFragment())
                    .addToBackStack(null)
                    .commit()

            }.onFailure { error ->
                Log.e("NFC", "Registration Failure: ${error.message}")
                // 5. 실패 -> 실패 화면으로 전환
                parentFragmentManager.beginTransaction()
                    .replace((requireView().parent as ViewGroup).id, DeviceFailureFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    // NFC 태그의 byte[] ID를 16진수 문자열로 변환하는 헬퍼 함수
    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "_" + String.format("%02X", it) }.drop(1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
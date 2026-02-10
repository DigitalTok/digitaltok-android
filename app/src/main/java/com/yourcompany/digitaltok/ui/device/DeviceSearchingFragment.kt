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
import com.yourcompany.digitaltok.ui.MainUiViewModel

class DeviceSearchingFragment : Fragment() {

    private var _binding: FragmentDeviceSearchingBinding? = null
    private val binding get() = _binding!!

    // 서버 통신을 위한 ViewModel
    private val deviceViewModel: DeviceViewModel by viewModels()
    // MainActivity와 NFC 태그 정보를 공유하기 위한 ViewModel
    private val nfcViewModel: NfcViewModel by activityViewModels()
    // HomeScreen(Compose)와 상태 공유
    private val mainUiViewModel: MainUiViewModel by activityViewModels()

    // NFC 관련 객체
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    // 태그 감지 시 UID를 임시 저장할 변수
    private var detectedNfcUid: String? = null

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
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "이 기기는 NFC를 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

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
        // 1. MainActivity가 전달한 NFC 태그 관찰
        nfcViewModel.tag.observe(viewLifecycleOwner) { tag ->
            if (tag != null) {
                val uid = bytesToHexString(tag.id)
                detectedNfcUid = uid // UID 임시 저장
                Log.d("NFC", "Tag detected with UID: $uid. Checking if registered.")

                // 2. 기기 등록 여부 확인 요청
                deviceViewModel.getDeviceByNfcUid(uid)

                nfcViewModel.tagHandled() // 태그 처리 완료
            }
        }

        // 3. 기기 조회 결과 관찰
        deviceViewModel.deviceDetailsResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { deviceData ->
                // 3-1. 조회 성공 -> 이미 등록된 기기 -> 성공 화면으로
                Log.d("NFC", "Device already registered: $deviceData")
                navigateToSuccess()
            }.onFailure { error ->
                // 3-2. 조회 실패
                Log.e("NFC", "Get device failed: ${error.message}")
                if (error.message?.contains("404") == true) {
                    // 404 에러 (Not Found) -> 미등록 기기이므로 등록 절차 시작
                    Log.d("NFC", "Device not found. Attempting to register.")
                    detectedNfcUid?.let { deviceViewModel.registerDevice(it) }
                } else {
                    // 그 외 다른 에러 (네트워크 등) -> 실패 화면으로
                    navigateToFailure()
                }
            }
        }

        // 4. 기기 등록 결과 관찰
        deviceViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { deviceData ->
                // 4-1. 등록 성공 -> 성공 화면으로
                Log.d("NFC", "Registration successful: $deviceData")
                navigateToSuccess()
            }.onFailure { error ->
                // 4-2. 등록 실패
                Log.e("NFC", "Registration failed: ${error.message}")
                // "기기가 이미 연결되어 있습니다" 오류는 성공으로 간주하고 성공 화면으로 이동
                if (error.message?.contains("DEVICE400") == true || error.message?.contains("기기가 이미 연결되어 있습니다") == true) {
                    Log.d("NFC", "Registration failed but device is already connected. Navigating to success.")
                    navigateToSuccess()
                } else {
                    // 그 외 다른 등록 오류는 실패 화면으로
                    navigateToFailure()
                }
            }
        }
    }

    private fun navigateToSuccess() {
        if (isAdded) { // Fragment가 Activity에 추가되었는지 확인
            mainUiViewModel.updateDeviceConnected(true)
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceSuccessFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToFailure() {
        if (isAdded) { // Fragment가 Activity에 추가되었는지 확인
            mainUiViewModel.updateDeviceConnected(false)
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceFailureFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "_" + String.format("%02X", it) }.drop(1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

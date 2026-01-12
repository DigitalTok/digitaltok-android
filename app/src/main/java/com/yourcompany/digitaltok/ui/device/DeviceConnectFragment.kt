package com.yourcompany.digitaltok.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yourcompany.digitaltok.databinding.FragmentDeviceConnectBinding

class DeviceConnectFragment : Fragment() {

    private var _binding: FragmentDeviceConnectBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentDeviceConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 공용 상단 바의 제목을 이 화면에 맞게 변경합니다.
        binding.topAppBar.titleTextView.text = "장치 (Device)"

        // 뒤로가기 버튼 클릭 이벤트 설정
        binding.topAppBar.backButton.setOnClickListener {
            // 이전 화면으로 돌아가는 동작을 처리합니다.
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // NFC 연결 시작 버튼 클릭 이벤트 설정
        binding.nfcConnectButton.setOnClickListener {
            // TODO: NFC 연결 시작 로직 구현
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
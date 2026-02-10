package com.yourcompany.digitaltok.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceSuccessBinding
import com.yourcompany.digitaltok.ui.MainUiViewModel

class DeviceSuccessFragment : Fragment() {

    private var _binding: FragmentDeviceSuccessBinding? = null
    private val binding get() = _binding!!

    // Compose UI와 상태를 공유하기 위해 MainUiViewModel 사용
    private val mainUiViewModel: MainUiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 기기 연결에 성공했으므로, ViewModel의 상태를 '연결됨'으로 업데이트
        mainUiViewModel.updateDeviceConnected(true)

        // TopAppBar의 뒤로가기 버튼은 홈으로 이동
        binding.successTopAppBar.backButton.setOnClickListener {
            mainUiViewModel.requestNavigate("home")
        }

        // "꾸미기" 버튼 클릭 시 꾸미기 탭으로 이동
        binding.decoButton.setOnClickListener {
            mainUiViewModel.requestNavigate("decorate")
        }

        // "홈으로 돌아가기" 버튼 클릭 시 홈 탭으로 이동
        binding.homeButton.setOnClickListener {
            mainUiViewModel.requestNavigate("home")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

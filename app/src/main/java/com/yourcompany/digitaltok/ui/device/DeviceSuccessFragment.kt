package com.yourcompany.digitaltok.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceSuccessBinding
import com.yourcompany.digitaltok.ui.MainViewModel

class DeviceSuccessFragment : Fragment() {

    private var _binding: FragmentDeviceSuccessBinding? = null
    private val binding get() = _binding!!

    // Activity와 상태를 공유하기 위한 MainViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

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

        // 1. 기기 연결에 성공했으므로, ViewModel에 상태 업데이트를 요청
        mainViewModel.setDeviceConnected(true)

        // TopAppBar의 뒤로가기 버튼 클릭 리스너 설정
        binding.successTopAppBar.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // "꾸미기" 버튼 클릭 리스너 (동작 미지정)
        binding.decoButton.setOnClickListener {
            Toast.makeText(requireContext(), "꾸미기 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
            // TODO: 꾸미기 화면으로 이동하는 네비게이션 로직 추가
        }

        // "홈으로 나가기" 버튼 클릭 리스너 설정
        binding.homeButton.setOnClickListener {
            // '연결' 과정에서 쌓인 모든 프래그먼트를 종료하고 홈으로 돌아감
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

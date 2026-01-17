package com.yourcompany.digitaltok.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yourcompany.digitaltok.databinding.FragmentDeviceFailureBinding
import com.yourcompany.digitaltok.ui.faq.HelpFragment

class DeviceFailureFragment : Fragment() {

    private var _binding: FragmentDeviceFailureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceFailureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TopAppBar의 뒤로가기 버튼 클릭 리스너 설정
        binding.failureTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceSearchingFragment())
                .commit()
        }

        // "다시 시도하기" 버튼 클릭 리스너 설정
        binding.restartButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceSearchingFragment())
                .commit()
        }

        // "도움말 & 고객지원" 버튼 클릭 리스너 설정
        binding.helpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, HelpFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

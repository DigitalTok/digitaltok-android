package com.yourcompany.digitaltok.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yourcompany.digitaltok.databinding.FragmentSupportBinding

class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단바 타이틀
        binding.connectTopAppBar.titleTextView.text = "고객 지원"

        // 뒤로가기 (Fragment니까 popBackStack이 자연스러움)
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

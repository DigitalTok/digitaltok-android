package com.yourcompany.digitaltok.ui.faq

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yourcompany.digitaltok.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단바
        binding.connectTopAppBar.titleTextView.text = "설정"
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.rowFaq.setOnClickListener {
            startActivity(Intent(requireContext(), FaqActivity::class.java))
        }
        binding.tvPersonal.paintFlags =
            binding.tvPersonal.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.rowSupport.setOnClickListener {
            startActivity(Intent(requireContext(), SupportActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

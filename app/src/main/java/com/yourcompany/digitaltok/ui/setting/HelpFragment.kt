package com.yourcompany.digitaltok.ui.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yourcompany.digitaltok.databinding.FragmentHelpBinding
import com.yourcompany.digitaltok.ui.profile.ProfileEditFragment

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

        // HelpFragment가 올라가 있는 FragmentContainerView id
        val containerId = (view.parent as View).id

        // 상단바
        binding.connectTopAppBar.titleTextView.text = "설정"
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 프로필 편집 → ProfileEditFragment
        binding.btnEditProfile.setOnClickListener {
            if (containerId != View.NO_ID) {
                parentFragmentManager.beginTransaction()
                    .replace(containerId, ProfileEditFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        // FAQ로 이동 → FaqFragment
        binding.rowFaq.setOnClickListener {
            if (containerId != View.NO_ID) {
                parentFragmentManager.beginTransaction()
                    .replace(containerId, FaqFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Support로 이동 → SupportFragment
        binding.rowSupport.setOnClickListener {
            if (containerId != View.NO_ID) {
                parentFragmentManager.beginTransaction()
                    .replace(containerId, SupportFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

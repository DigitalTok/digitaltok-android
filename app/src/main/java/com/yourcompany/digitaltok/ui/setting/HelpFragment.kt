package com.yourcompany.digitaltok.ui.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.databinding.FragmentHelpBinding
import com.yourcompany.digitaltok.ui.profile.ProfileEditFragment
import com.yourcompany.digitaltok.ui.setting.SupportFragment
import com.yourcompany.digitaltok.data.repository.UserRepository
import kotlinx.coroutines.launch

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository

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

        userRepository = UserRepository(requireContext().applicationContext)

        // (테스트) 화면 진입 시 사용자 정보 연동
        loadUserInfo()

        // HelpFragment가 올라가 있는 FragmentContainerView id
        val containerId = (view.parent as View).id

        // 상단바
        binding.connectTopAppBar.titleTextView.text = "설정"
        binding.connectTopAppBar.backButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
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

    private fun loadUserInfo() {
        lifecycleScope.launch {
            userRepository.getMyProfile()
                .onSuccess { me ->
                    // 테스트 성공 기준:
                    // UserRepository가 TEST MODE면 여기로 들어와서
                    // 화면에 "테스트닉네임 / test@example.com" 같은 값이 보이면 성공

                    binding.tvUserName.text = me.nickname
                    binding.tvUserEmail.text = me.email

                    android.util.Log.d("HelpFragment", "TEST/UI BIND OK: ${me.nickname}, ${me.email}")
                }
                .onFailure { e ->
                    android.util.Log.e("HelpFragment", "getMyProfile failed", e)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
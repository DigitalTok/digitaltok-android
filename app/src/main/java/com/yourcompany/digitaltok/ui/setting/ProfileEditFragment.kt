package com.yourcompany.digitaltok.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yourcompany.digitaltok.databinding.FragmentProfileEditBinding

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단바 타이틀
        binding.connectTopAppBar.titleTextView.text = "프로필 편집"

        // 상단바 뒤로가기
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 로그아웃 클릭 → 다이얼로그
        binding.tvLogout.setOnClickListener {
            showLogoutDialog()
        }

        // 회원 탈퇴 클릭 → 방금 만든 다이얼로그
        binding.tvWithdraw.setOnClickListener {
            showWithdrawDialog()
        }
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(com.yourcompany.digitaltok.R.layout.dialog_logout, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnLogout).setOnClickListener {
            dialog.dismiss()
            // TODO: 실제 로그아웃 처리
        }

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 추가: 회원탈퇴 다이얼로그
    private fun showWithdrawDialog() {
        val dialogView = layoutInflater.inflate(com.yourcompany.digitaltok.R.layout.dialog_withdraw, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // 둥근 모서리/디자인 유지용 (배경 투명)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnWithdraw).setOnClickListener {
            dialog.dismiss()
            // TODO: 실제 회원탈퇴 처리
        }

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

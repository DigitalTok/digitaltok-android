package com.yourcompany.digitaltok.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.databinding.FragmentProfileEditBinding
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.network.RetrofitClient
import com.yourcompany.digitaltok.data.repository.AccountRepository
import com.yourcompany.digitaltok.data.repository.AuthLocalStore
import com.yourcompany.digitaltok.data.repository.PrefsAuthLocalStore
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var accountRepository: AccountRepository

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

        binding.connectTopAppBar.titleTextView.text = "프로필 편집"
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Repository 초기화
        val accountApi = RetrofitClient.create(AccountApiService::class.java)
        val localStore: AuthLocalStore = PrefsAuthLocalStore(requireContext().applicationContext)
        accountRepository = AccountRepository(accountApi, localStore)

        binding.tvLogout.setOnClickListener { showLogoutDialog() }
        binding.tvWithdraw.setOnClickListener { showWithdrawDialog() }


        // TOKEN_CHECK
        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refreshToken", null)
        android.util.Log.d("TOKEN_CHECK", "refreshToken = $refreshToken")

    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(com.yourcompany.digitaltok.R.layout.dialog_logout, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnLogout).setOnClickListener {
            dialog.dismiss()

            lifecycleScope.launch {
                accountRepository.logout()
                    .onSuccess { moveToStart() }
                    .onFailure { showError(it.message ?: "로그아웃 실패") }
            }
        }

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showWithdrawDialog() {
        val dialogView = layoutInflater.inflate(com.yourcompany.digitaltok.R.layout.dialog_withdraw, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnWithdraw).setOnClickListener {
            dialog.dismiss()

            lifecycleScope.launch {
                accountRepository.withdraw()
                    .onSuccess { moveToStart() }
                    .onFailure { showError(it.message ?: "회원탈퇴 실패") }
            }
        }

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 로그아웃/탈퇴 후 이동 화면
    private fun moveToStart() {
        val intent = Intent(requireContext(), com.yourcompany.digitaltok.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

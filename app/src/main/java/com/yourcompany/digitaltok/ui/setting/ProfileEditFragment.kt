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

        binding.ivEditEmail.setOnClickListener {
            showChangeEmailDialog()
        }

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

    private fun showChangeEmailDialog() {
        val dialogView = layoutInflater.inflate(com.yourcompany.digitaltok.R.layout.dialog_change_email, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etPassword = dialogView.findViewById<android.widget.EditText>(com.yourcompany.digitaltok.R.id.etPassword)
        val etNewEmail = dialogView.findViewById<android.widget.EditText>(com.yourcompany.digitaltok.R.id.etNewEmail)

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(com.yourcompany.digitaltok.R.id.btnChange).setOnClickListener {
            val password = etPassword.text?.toString().orEmpty()
            val newEmail = etNewEmail.text?.toString().orEmpty()

            if (password.isBlank()) {
                showError("기존 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }
            if (newEmail.isBlank()) {
                showError("새 이메일을 입력해주세요.")
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                showError("이메일 형식이 올바르지 않습니다.")
                return@setOnClickListener
            }

            // API 호출
            lifecycleScope.launch {
                accountRepository.changeEmail(password, newEmail)
                    .onSuccess {
                        dialog.dismiss()
                        binding.tvEmailValue.text = newEmail
                        Toast.makeText(requireContext(), "이메일이 변경되었습니다.", Toast.LENGTH_SHORT).show()

                        // 화면 표시 갱신 (너의 이메일 TextView id로 변경)
                        binding.tvEmailValue.text = newEmail

                        android.widget.Toast.makeText(requireContext(), "이메일이 변경되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    .onFailure { e ->
                        val msg = when (e) {
                            is retrofit2.HttpException -> {
                                when (e.code()) {
                                    401, 403 -> "기존 비밀번호가 올바르지 않습니다."
                                    400 -> "요청 값이 올바르지 않습니다."
                                    409 -> "이미 사용 중인 이메일입니다."
                                    else -> "이메일 변경에 실패했습니다. (${e.code()})"
                                }
                            }
                            else -> e.message ?: "이메일 변경에 실패했습니다."
                        }
                        showError(msg)
                    }
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

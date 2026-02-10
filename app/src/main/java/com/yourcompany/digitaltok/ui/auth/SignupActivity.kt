package com.yourcompany.digitaltok.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private val GREEN = Color.parseColor("#00C950")
    private val RED = Color.parseColor("#FB2C36")

    private var isEmailChecked = false
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isPasswordMatch = false

    private lateinit var btnBack: TextView
    private lateinit var tvGoLogin: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnCheckEmail: Button
    private lateinit var tvEmailStatus: TextView
    private lateinit var etPassword: EditText
    private lateinit var tvPwStatus: TextView
    private lateinit var etPasswordConfirm: EditText
    private lateinit var tvPwConfirmStatus: TextView
    private lateinit var cbTerms1: CheckBox
    private lateinit var cbTerms2: CheckBox
    private lateinit var cbTerms3: CheckBox
    private lateinit var btnSignup: Button

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnBack = findViewById(R.id.btnBack)
        tvGoLogin = findViewById(R.id.tvGoLogin)
        etEmail = findViewById(R.id.etEmail)
        btnCheckEmail = findViewById(R.id.btnCheckEmail)
        tvEmailStatus = findViewById(R.id.tvEmailStatus)
        etPassword = findViewById(R.id.etPassword)
        tvPwStatus = findViewById(R.id.tvPwStatus)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        tvPwConfirmStatus = findViewById(R.id.tvPwConfirmStatus)
        cbTerms1 = findViewById(R.id.cbTerms1)
        cbTerms2 = findViewById(R.id.cbTerms2)
        cbTerms3 = findViewById(R.id.cbTerms3)
        btnSignup = findViewById(R.id.btnSignup)

        // ✅ 상단 "뒤로" -> 이전 화면
        btnBack.setOnClickListener { finish() }

        // ✅ 하단 "로그인" -> 로그인 화면 이동
        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
            finish()
        }

        setupRequiredTagColor()
        setupInitialUi()
        setupListeners()
        updateSignupButton()
    }

    private fun setupInitialUi() {
        btnCheckEmail.isEnabled = false
        btnCheckEmail.alpha = 0.5f
        btnCheckEmail.text = "중복확인"

        tvEmailStatus.visibility = View.GONE
        tvPwStatus.visibility = View.GONE
        tvPwConfirmStatus.visibility = View.GONE

        btnSignup.isEnabled = false
    }

    private fun setupRequiredTagColor() {
        setRequiredColor(cbTerms1)
        setRequiredColor(cbTerms2)
    }

    private fun setRequiredColor(cb: CheckBox) {
        val text = cb.text.toString()
        val required = "[필수]"
        val start = text.indexOf(required)
        if (start == -1) return
        val end = start + required.length

        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(RED),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        cb.text = spannable
    }

    private fun setupListeners() {
        etEmail.addTextChangedListener(SimpleTextWatcher { raw ->
            val email = raw.trim()

            // 이메일 바뀌면 중복확인 다시 해야 함
            isEmailChecked = false
            btnCheckEmail.text = "중복확인"

            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            btnCheckEmail.isEnabled = isEmailValid
            btnCheckEmail.alpha = if (isEmailValid) 1.0f else 0.5f

            tvEmailStatus.visibility = View.GONE
            updateSignupButton()
        })

        btnCheckEmail.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showEmailStatus("이메일 형식을 확인해주세요.", false)
                return@setOnClickListener
            }
            // ✅ 1번 API: 이메일 중복확인
            checkEmailDuplicate(email)
        }

        etPassword.addTextChangedListener(SimpleTextWatcher { pw ->
            isPasswordValid = pw.length >= 6
            tvPwStatus.visibility = View.VISIBLE
            tvPwStatus.setTextColor(if (isPasswordValid) GREEN else RED)
            tvPwStatus.text = if (isPasswordValid) "사용 가능한 비밀번호예요." else "비밀번호는 6자 이상이어야 해요."
            updateSignupButton()
        })

        etPasswordConfirm.addTextChangedListener(SimpleTextWatcher {
            isPasswordMatch = etPassword.text.toString() == etPasswordConfirm.text.toString()
            tvPwConfirmStatus.visibility = View.VISIBLE
            tvPwConfirmStatus.setTextColor(if (isPasswordMatch) GREEN else RED)
            tvPwConfirmStatus.text = if (isPasswordMatch) "비밀번호가 일치해요." else "비밀번호가 일치하지 않아요."
            updateSignupButton()
        })

        cbTerms1.setOnCheckedChangeListener { _, _ -> updateSignupButton() }
        cbTerms2.setOnCheckedChangeListener { _, _ -> updateSignupButton() }
        cbTerms3.setOnCheckedChangeListener { _, _ -> updateSignupButton() }

        btnSignup.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pw = etPassword.text.toString()

            // ✅ 2번 API: 회원가입
            // Swagger에 phoneNumber가 required로 보이는 경우가 많아서 임시값 넣어둠.
            // 너 UI에 전화번호 입력이 있으면 그 값으로 바꾸면 끝!
            val phoneNumber = "000-0000-0000"

            signup(email, pw, phoneNumber)
        }
    }

    private fun updateSignupButton() {
        val requiredAgreed = cbTerms1.isChecked && cbTerms2.isChecked
        val enabled = isEmailChecked && isPasswordValid && isPasswordMatch && requiredAgreed
        btnSignup.isEnabled = enabled
    }

    // -------------------------
    // API 1) 이메일 중복확인
    // -------------------------
    private fun checkEmailDuplicate(email: String) {
        btnCheckEmail.isEnabled = false
        btnCheckEmail.alpha = 0.5f

        lifecycleScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    authRepository.duplicateCheck(email)
                }

                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    // ✅ 사용 가능 (200)
                    isEmailChecked = true
                    btnCheckEmail.text = "확인완료"
                    showEmailStatus("사용 가능한 이메일이에요.", true)
                } else {
                    // 보통 중복이면 409, 아니면 400/404 등
                    isEmailChecked = false
                    btnCheckEmail.text = "중복확인"
                    val msg = when (res.code()) {
                        409 -> "이미 사용 중인 이메일이에요."
                        else -> "중복확인 실패 (HTTP ${res.code()})"
                    }
                    showEmailStatus(msg, false)
                }
            } catch (e: Exception) {
                isEmailChecked = false
                btnCheckEmail.text = "중복확인"
                showEmailStatus("네트워크 오류: ${e.message}", false)
            } finally {
                // 다시 눌러볼 수 있게
                btnCheckEmail.isEnabled = isEmailValid
                btnCheckEmail.alpha = if (isEmailValid) 1.0f else 0.5f
                updateSignupButton()
            }
        }
    }

    private fun showEmailStatus(text: String, ok: Boolean) {
        tvEmailStatus.visibility = View.VISIBLE
        tvEmailStatus.setTextColor(if (ok) GREEN else RED)
        tvEmailStatus.text = text
    }

    // -------------------------
    // API 2) 회원가입
    // -------------------------
    private fun signup(email: String, password: String, phoneNumber: String) {
        btnSignup.isEnabled = false

        lifecycleScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    authRepository.signup(email, password, phoneNumber)
                }

                if (!res.isSuccessful) {
                    Toast.makeText(
                        this@SignupActivity,
                        "회원가입 실패 (HTTP ${res.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateSignupButton()
                    return@launch
                }

                val body = res.body()
                if (body?.isSuccess == true) {
                    Toast.makeText(this@SignupActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    // 회원가입 성공 -> 로그인 화면으로
                    startActivity(Intent(this@SignupActivity, EmailLoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        body?.message ?: "회원가입 실패",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateSignupButton()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SignupActivity,
                    "네트워크 오류: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                updateSignupButton()
            } finally {
                // enabled는 updateSignupButton이 결정
                // 여기서 true로 강제하지 않음
            }
        }
    }

    private class SimpleTextWatcher(
        private val onChanged: (String) -> Unit
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            onChanged(s?.toString() ?: "")
        }
    }
}

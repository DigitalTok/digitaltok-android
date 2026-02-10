package com.yourcompany.digitaltok.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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

class EmailLoginActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbAutoLogin: CheckBox
    private lateinit var tvFindPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView

    private var isEmailValid = false
    private var isPwValid = false

    // ✅ Repository
    private val authRepository = AuthRepository()

    // ✅ SharedPreferences (토큰/자동로그인 저장)
    private val prefs by lazy { getSharedPreferences("auth_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        btnBack = findViewById(R.id.btnBack)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cbAutoLogin = findViewById(R.id.cbAutoLogin)
        tvFindPassword = findViewById(R.id.tvFindPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)

        // ✅ 상단 뒤로 -> 이전 화면
        btnBack.setOnClickListener { finish() }

        // ✅ 시작은 비활성
        setLoginEnabled(false)

        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pw = etPassword.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw.isBlank()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ 로그인 API 호출
            login(email, pw)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvFindPassword.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }
    }

    private fun login(email: String, password: String) {
        btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                Log.d("LOGIN_TEST", "로그인 요청 시작 email=$email")

                val response = withContext(Dispatchers.IO) {
                    authRepository.login(email, password)
                }

                Log.d("LOGIN_TEST", "로그인 HTTP code=${response.code()}")

                if (!response.isSuccessful) {
                    Toast.makeText(
                        this@EmailLoginActivity,
                        "로그인 실패 (HTTP ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                    setLoginEnabled(isEmailValid && isPwValid)
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    Toast.makeText(this@EmailLoginActivity, "응답이 비어있어요.", Toast.LENGTH_SHORT).show()
                    setLoginEnabled(isEmailValid && isPwValid)
                    return@launch
                }

                if (body.isSuccess != true) {
                    Toast.makeText(
                        this@EmailLoginActivity,
                        body.message ?: "로그인 실패",
                        Toast.LENGTH_SHORT
                    ).show()
                    setLoginEnabled(isEmailValid && isPwValid)
                    return@launch
                }

                val result = body.result
                if (result == null) {
                    Toast.makeText(this@EmailLoginActivity, "로그인 결과가 비어있어요.", Toast.LENGTH_SHORT).show()
                    setLoginEnabled(isEmailValid && isPwValid)
                    return@launch
                }

                val accessToken = result.accessToken
                val refreshToken = result.refreshToken

                if (accessToken.isBlank() || refreshToken.isBlank()) {
                    Toast.makeText(
                        this@EmailLoginActivity,
                        "토큰이 없어요. 응답 필드를 확인해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    setLoginEnabled(isEmailValid && isPwValid)
                    return@launch
                }

                // ✅ 저장: 토큰 + 자동로그인 여부
                saveAuth(accessToken, refreshToken, cbAutoLogin.isChecked)

                // ✅ 여기까지 오면 "연동 성공" 확정
                Toast.makeText(this@EmailLoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()

                // ✅ MainActivity로 "성공" 결과 반환
                val data = Intent().putExtra("login_success", true)
                setResult(Activity.RESULT_OK, data)
                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@EmailLoginActivity,
                    "네트워크 오류: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("LOGIN_TEST", "로그인 예외", e)
                setLoginEnabled(isEmailValid && isPwValid)
            }
        }
    }

    private fun saveAuth(accessToken: String, refreshToken: String, autoLogin: Boolean) {
        prefs.edit()
            .putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .putBoolean("autoLogin", autoLogin)
            .apply()
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            val email = etEmail.text.toString().trim()
            val pw = etPassword.text.toString()

            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            isPwValid = pw.isNotEmpty()

            setLoginEnabled(isEmailValid && isPwValid)
        }
    }

    private fun setLoginEnabled(enabled: Boolean) {
        btnLogin.isEnabled = enabled
        btnLogin.setBackgroundResource(
            if (enabled) R.drawable.bg_btn_blue else R.drawable.bg_btn_gray
        )
    }
}

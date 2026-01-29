package com.yourcompany.digitaltok.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.digitaltok.MainActivity
import com.yourcompany.digitaltok.R

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
            // TODO: 실제 로그인 API 붙일 자리
            // cbAutoLogin.isChecked 값을 저장해서 "자동 로그인"도 나중에 가능

            openHomeAndClearBackStack()
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvFindPassword.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }
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

    private fun openHomeAndClearBackStack() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("go_home", true)
            // ✅ 뒤로 눌러도 로그인으로 돌아가지 않게 "작업(task)" 정리
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }
        startActivity(intent)
        finish()
    }
}

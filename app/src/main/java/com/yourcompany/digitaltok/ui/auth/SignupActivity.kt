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
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.digitaltok.R

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
            isEmailChecked = false
            btnCheckEmail.text = "중복확인"

            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            btnCheckEmail.isEnabled = isEmailValid
            btnCheckEmail.alpha = if (isEmailValid) 1.0f else 0.5f

            updateSignupButton()
        })

        btnCheckEmail.setOnClickListener {
            isEmailChecked = true
            updateSignupButton()
        }

        etPassword.addTextChangedListener(SimpleTextWatcher { pw ->
            isPasswordValid = pw.length >= 6
            updateSignupButton()
        })

        etPasswordConfirm.addTextChangedListener(SimpleTextWatcher {
            isPasswordMatch = etPassword.text.toString() == etPasswordConfirm.text.toString()
            updateSignupButton()
        })

        cbTerms1.setOnCheckedChangeListener { _, _ -> updateSignupButton() }
        cbTerms2.setOnCheckedChangeListener { _, _ -> updateSignupButton() }
        cbTerms3.setOnCheckedChangeListener { _, _ -> updateSignupButton() }

        btnSignup.setOnClickListener {
            // TODO: 회원가입 성공 -> 로그인 화면으로
            startActivity(Intent(this, EmailLoginActivity::class.java))
            finish()
        }
    }

    private fun updateSignupButton() {
        val requiredAgreed = cbTerms1.isChecked && cbTerms2.isChecked
        val enabled = isEmailChecked && isPasswordValid && isPasswordMatch && requiredAgreed
        btnSignup.isEnabled = enabled
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

package com.yourcompany.digitaltok.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.digitaltok.R

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var etResetEmail: EditText
    private lateinit var btnSendLink: Button
    private lateinit var btnCancel: Button
    private lateinit var tvResetStatus: TextView

    private var isEmailValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        btnBack = findViewById(R.id.btnBack)
        etResetEmail = findViewById(R.id.etResetEmail)
        btnSendLink = findViewById(R.id.btnSendLink)
        btnCancel = findViewById(R.id.btnCancel)
        tvResetStatus = findViewById(R.id.tvResetStatus)

        btnBack.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        setSendEnabled(false)

        etResetEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = etResetEmail.text.toString().trim()
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                setSendEnabled(isEmailValid)
                if (!isEmailValid) tvResetStatus.visibility = View.GONE
            }
        })

        btnSendLink.setOnClickListener {
            // TODO: 서버에 재설정 링크 요청 API 붙일 자리
            tvResetStatus.visibility = View.VISIBLE
        }
    }

    private fun setSendEnabled(enabled: Boolean) {
        btnSendLink.isEnabled = enabled
        btnSendLink.setBackgroundResource(
            if (enabled) R.drawable.bg_btn_blue else R.drawable.bg_btn_gray
        )
    }
}

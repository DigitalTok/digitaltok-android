package com.yourcompany.digitaltok

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 너가 만든 회원가입 XML 레이아웃 파일명으로 맞춰줘!
        // 예: activity_signup.xml 이면 R.layout.activity_signup
        setContentView(R.layout.activity_signup)

        // ✅ 너 XML의 체크박스 id로 맞춰줘 (아래 3개 id는 예시)
        val cb1 = findViewById<CheckBox>(R.id.cbTerms1)
        val cb2 = findViewById<CheckBox>(R.id.cbTerms2)
        val cb3 = findViewById<CheckBox>(R.id.cbTerms3)

        setRequiredColor(cb1)
        setRequiredColor(cb2)
        setRequiredColor(cb3)
    }

    private fun setRequiredColor(cb: CheckBox) {
        val text = cb.text.toString()
        val required = "[필수]"

        val start = text.indexOf(required)
        if (start == -1) return
        val end = start + required.length

        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FB2C36")),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        cb.text = spannable
    }
}

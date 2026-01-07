package com.example.digitaltok

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FAQ 화면으로 바로 이동
        startActivity(Intent(this, FaqActivity::class.java))

        // MainActivity는 종료 (뒤로가기 시 Hello 화면으로 돌아가지 않게)
        finish()
    }
}

// MainActivity.kt : 오른쪽(HELP) 버튼 클릭 시 activity_help.xml(HelpActivity)로 이동
package com.example.digitaltok

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.digitaltok.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNavHelp.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
    }
}

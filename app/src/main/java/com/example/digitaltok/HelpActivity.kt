package com.example.digitaltok

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.digitaltok.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rowFaq.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java))
        }

        binding.rowSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
        }
    }
}

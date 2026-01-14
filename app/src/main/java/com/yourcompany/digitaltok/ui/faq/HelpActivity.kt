package com.yourcompany.digitaltok.ui.faq

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.digitaltok.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectTopAppBar.titleTextView.text = "설정"
        binding.connectTopAppBar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rowFaq.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java))
        }

        binding.rowSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
        }
    }
}

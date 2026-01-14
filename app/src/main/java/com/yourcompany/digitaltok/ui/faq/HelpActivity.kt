package com.yourcompany.digitaltok.ui.faq

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.digitaltok.databinding.FragmentHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: FragmentHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectTopAppBar.titleTextView.text = "설정"
        binding.connectTopAppBar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvPersonal.paintFlags =
            binding.tvPersonal.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        binding.rowFaq.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java))
        }

        binding.rowSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
        }
    }
}

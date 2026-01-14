package com.yourcompany.digitaltok.ui.faq

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.yourcompany.digitaltok.databinding.ActivitySupportBinding

class SupportActivity : ComponentActivity() {

    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectTopAppBar.titleTextView.text = "고객 지원"
        binding.connectTopAppBar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

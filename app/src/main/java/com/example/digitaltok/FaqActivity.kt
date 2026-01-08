package com.example.digitaltok

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitaltok.databinding.ActivityFaqBinding
import com.example.digitaltok.ui.theme.FaqItem
import androidx.activity.ComponentActivity
import com.example.digitaltok.LastItemBottomSpaceDecoration

class FaqActivity : ComponentActivity() {

    private lateinit var binding: ActivityFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val faqList = listOf(
            FaqItem(
                "DigitalTok은 어떻게 사용하나요?",
                "앱에서 이미지나 템플릿을 선택한 후 NFC로 DigitalTok에 전송하면 자동으로 표시됩니다."
            ),
            FaqItem(
                "그립톡과 연결이 안 돼요",
                "휴대폰 NFC가 켜져 있는지 확인하고, DigitalTok을 휴대폰 뒷면에 가까이 대주세요."
            ),
            FaqItem(
                "이미지가 표시되지 않아요",
                "이미지 크기/형식 문제일 수 있습니다. JPG 또는 PNG를 사용해 주세요."
            ),
            FaqItem(
                "여러 개의 DigitalTok을 연결할 수 있나요?",
                "현재는 하나의 DigitalTok만 연결할 수 있습니다."
            )
        )
        val bottomPx = (140 * resources.displayMetrics.density).toInt()
        binding.rvFaq.addItemDecoration(LastItemBottomSpaceDecoration(bottomPx))


        binding.rvFaq.layoutManager = LinearLayoutManager(this)
        binding.rvFaq.adapter = FaqAdapter(faqList)
    }
}

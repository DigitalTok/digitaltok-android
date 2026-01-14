package com.yourcompany.digitaltok.ui.faq

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitaltok.ui.theme.FaqItem
import androidx.activity.ComponentActivity
import com.yourcompany.digitaltok.databinding.FragmentFaqBinding

class FaqActivity : ComponentActivity() {

    private lateinit var binding: FragmentFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectTopAppBar.titleTextView.text = "자주 묻는 질문"
        binding.connectTopAppBar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val faqList = listOf(
            FaqItem(
                "디링은 어떻게 사용하나요?",
                "앱에서 이미지나 템플릿을 선택한 후 NFC로 디링에 전송하면 자동으로 표시됩니다."
            ),
            FaqItem(
                "디링과 연결이 안 돼요",
                "휴대폰 NFC가 켜져 있는지 확인하고, 디링을 휴대폰 뒷면에 가까이 대주세요."
            ),
            FaqItem(
                "이미지가 표시되지 않아요",
                "이미지 크기가 너무 크거나 지원하지 않는 형식일 수 있습니다. JPG, PNG 형식을 사용해주세요."
            ),
            FaqItem(
                "여러 개의 디링을 연결할 수 있나요?",
                "현재는 하나의 디링에 연결할 수 있습니다."
            )
        )
        val bottomPx = (140 * resources.displayMetrics.density).toInt()
        binding.rvFaq.addItemDecoration(LastItemBottomSpaceDecoration(bottomPx))


        binding.rvFaq.layoutManager = LinearLayoutManager(this)
        binding.rvFaq.adapter = FaqAdapter(
            items = faqList,
            onSupportClick = {
                // "고객 지원" 화면 전환
                val intent = Intent(this, SupportActivity::class.java)
                startActivity(intent)
            }
        )

    }
}

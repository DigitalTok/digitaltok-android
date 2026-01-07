package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.components.PageIndicator
import com.yourcompany.digitaltok.ui.components.PrimaryButton
import com.yourcompany.digitaltok.ui.components.SkipButton

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var page by remember { mutableStateOf(0) }

    // ✅ 4개 온보딩 페이지 데이터 (텍스트/색상/이미지)
    val pages = listOf(
        OnboardingPageData(
            title = "DigitalTok에\n오신 것을 환영합니다",
            desc = "전자 잉크 그립톡으로\n스마트한 소통을 시작하세요",
            gradient = listOf(Color(0xFF2B7FFF), Color(0xFF9810FA)),
            emoji = R.drawable.ic_onboarding_hand
        ),
        OnboardingPageData(
            title = "전자 잉크 디스플레이\n그림톡",
            desc = "저전력 전자 잉크로\n개성 있는 메시지를 표현하세요",
            gradient = listOf(Color(0xFFAD46FF), Color(0xFFE60076)),
            emoji = R.drawable.ic_onboarding_phone
        ),
        OnboardingPageData(
            title = "대중교통에서\n편리하게",
            desc = "하차 정보나 배려 메시지를\n손쉽게 전달할 수 있어요",
            gradient = listOf(Color(0xFFF6339A), Color(0xFFE7000B)),
            emoji = R.drawable.ic_onboarding_train
        ),
        OnboardingPageData(
            title = "사진과 템플릿\n자유롭게",
            desc = "나만의 사진이나 다양한 템플릿으로\n그림톡을 꾸며보세요",
            gradient = listOf(Color(0xFFFF6900), Color(0xFFD08700)),
            emoji = R.drawable.ic_onboarding_camera
        )
    )

    val isLast = page == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // 상단 "건너뛰기"
        SkipButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onSkip = onFinish
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 상단 여백
            Spacer(modifier = Modifier.height(32.dp))

            // ● 큰 원 + 이모지
            PageCircle(pages[page])

            // 중앙 텍스트 영역
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = pages[page].title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = pages[page].desc,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF4A5565),
                    textAlign = TextAlign.Center
                )
            }

            // 하단 인디케이터 + 버튼
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PageIndicator(
                    total = pages.size,
                    selected = page,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 이전 버튼
                    OutlinedButton(
                        onClick = { if (page > 0) page-- },
                        enabled = page > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(text = "이전")
                    }

                    // 다음 / 시작하기 버튼
                    PrimaryButton(
                        text = if (isLast) "시작하기" else "다음",
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLast) {
                            onFinish()
                        } else {
                            page++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PageCircle(data: OnboardingPageData) {
    Box(
        modifier = Modifier
            .size(288.dp)  // 피그마 원 288x288 근접
            .background(
                brush = Brush.linearGradient(data.gradient),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = data.emoji),
            contentDescription = null,
            modifier = Modifier.size(128.dp),   // 피그마 128x128
            contentScale = ContentScale.Fit
        )
    }
}

data class OnboardingPageData(
    val title: String,
    val desc: String,
    val gradient: List<Color>,
    val emoji: Int
)

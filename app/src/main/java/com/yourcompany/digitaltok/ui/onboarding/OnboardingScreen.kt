package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

private val pages = listOf(
    OnboardingPage(
        "디링에 오신 것을\n환영합니다",
        "스마트한 소통을 시작해보세요",
        R.drawable.banner_euljiro
    ),
    OnboardingPage(
        "전자 잉크 디스플레이 ‘디링’",
        "배터리 필요 없는 디스플레이로\n개성있는 메시지를 표현하세요",
        R.drawable.banner_euljiro
    ),
    OnboardingPage(
        "대중교통에서 편리하게",
        "하차 정보나 손쉽게 전달할 수 있어요",
        R.drawable.banner_euljiro
    )
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = pages[pagerState.currentPage].title,
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 31.2.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF121212),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.width(211.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = pages[pagerState.currentPage].subtitle,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF505050),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.width(211.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 64.dp),
            pageSpacing = 21.dp,
            modifier = Modifier.height(153.dp)
        ) { page ->
            BannerImageCard(pages[page].imageRes)
        }

        Spacer(Modifier.height(24.dp))

        CapsuleIndicator(total = pages.size, current = pagerState.currentPage)

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(49.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9E9E9))
            ) {
                Text("이전으로", color = Color(0xFF9E9E9E))
            }

            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinish()
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(49.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3AADFF))
            ) {
                Text("넘어가기", color = Color.White)
            }
        }
    }
}

@Composable
private fun BannerImageCard(imageRes: Int) {
    Box(
        modifier = Modifier
            .width(153.dp)
            .height(153.dp)
            .background(Color.White, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CapsuleIndicator(total: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(if (index == current) 24.dp else 10.dp)
                    .background(
                        if (index == current) Color(0xFF3AADFF) else Color(0xFFD9D9D9),
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}

package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    // ✅ 온보딩 4장(landing 제외)
    val pages = listOf(
        OnboardingPageData(
            title = "디링에 오신 것을\n환영합니다",
            subtitle = "스마트한 소통을 시작해보세요",
            imageRes = R.drawable.ic_onboard_message
        ),
        OnboardingPageData(
            title = "전자 잉크\n디스플레이 ‘디링’",
            subtitle = "배터리 필요 없는 디스플레이로\n개성있는 메시지를 표현하세요",
            imageRes = R.drawable.ic_onboard_display
        ),
        OnboardingPageData(
            title = "대중교통에서 편리하게",
            subtitle = "하차 정보나 메시지를\n손쉽게 전달할 수 있어요",
            showSubwayCards = true
        ),
        OnboardingPageData(
            title = "사진을 추가해보세요",
            subtitle = "앨범 속 사진으로\n디링을 꾸며보세요",
            imageRes = R.drawable.ic_onboard_image
        )
    )

    // ✅ 역 카드 6개
    val stationCards = listOf(
        R.drawable.img_station_239, // 홍대입구
        R.drawable.img_station_203, // 을지로3가
        R.drawable.img_station_222, // 강남
        R.drawable.img_station_216, // 잠실
        R.drawable.img_station_221, // 역삼
        R.drawable.img_station_219  // 삼성
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    // ✅ 3번(역) 페이지 캐러셀 상태를 한 번만 유지
    val stationListState = rememberLazyListState()
    val currentStationIndex by remember { derivedStateOf { stationListState.centerItemIndex() } }
    val lastStationIndex = stationCards.lastIndex

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]
            val isStationPage = pageIndex == 2

            // ✅ 피그마 느낌: 화면이 길쭉해 보이지 않게
            // 남는 공간을 위에서 먹고(Spacer weight), 콘텐츠 덩어리가 아래로 내려오도록 구성
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✅ 위 공간을 흡수해서 텍스트/이미지/버튼이 아래로 내려오게
                Spacer(Modifier.weight(1f))

                // ✅ 텍스트 블록
                Column(
                    modifier = Modifier.width(211.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        text = page.title,
                        style = TextStyle(
                            fontSize = 24.sp,
                            lineHeight = 31.2.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF121212),
                            textAlign = TextAlign.Center
                        ),
                        textAlign = TextAlign.Center
                    )

                    androidx.compose.material3.Text(
                        text = page.subtitle,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF505050),
                            textAlign = TextAlign.Center
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(48.dp)) // 텍스트-이미지 간격

                // ✅ 중앙 콘텐츠
                if (page.showSubwayCards) {
                    SubwayCarousel(
                        cards = stationCards,
                        listState = stationListState,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    page.imageRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.size(143.dp)
                        )
                    }
                }

                Spacer(Modifier.height(36.dp)) // 이미지-인디케이터 간격

                // ✅ 인디케이터(버튼 가까이)
                OnboardingIndicator(total = pages.size, current = pageIndex)

                Spacer(Modifier.height(16.dp))

                // ✅ 버튼(가운데 정렬, 바닥에 너무 붙지 않게)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ---- 이전으로 ----
                    OnboardingButton(
                        text = "이전으로",
                        containerColor = Color(0xFFE0E0E0),
                        textColor = Color(0xFF9E9E9E),
                        enabled = pageIndex != 0 || (isStationPage && currentStationIndex > 0)
                    ) {
                        scope.launch {
                            if (isStationPage && currentStationIndex > 0) {
                                // ✅ 역 페이지: 역을 먼저 뒤로
                                stationListState.animateScrollToItem(currentStationIndex - 1)
                            } else {
                                // ✅ 그 외: 온보딩 페이지를 뒤로
                                if (pageIndex > 0) pagerState.animateScrollToPage(pageIndex - 1)
                            }
                        }
                    }

                    // ---- 다음으로 ----
                    OnboardingButton(
                        text = "다음으로",
                        containerColor = Color(0xFF36ABFF),
                        textColor = Color.White,
                        enabled = true
                    ) {
                        scope.launch {
                            if (isStationPage && currentStationIndex < lastStationIndex) {
                                // ✅ 역 페이지: 역을 먼저 앞으로
                                stationListState.animateScrollToItem(currentStationIndex + 1)
                            } else {
                                // ✅ 역 마지막 or 역페이지 아님: 온보딩 다음으로
                                if (pageIndex == pages.lastIndex) {
                                    onFinish()
                                } else {
                                    pagerState.animateScrollToPage(pageIndex + 1)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp)) // 하단 여백(버튼이 너무 바닥에 붙지 않게)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubwayCarousel(
    cards: List<Int>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val fling = rememberSnapFlingBehavior(lazyListState = listState)

    LazyRow(
        state = listState,
        flingBehavior = fling,
        contentPadding = PaddingValues(horizontal = 64.dp), // 좌우 살짝 보이게
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(
            items = cards,
            key = { it }
        ) { resId ->
            // 중앙: 컬러/크게/진하게
            // 좌우: 흑백/작게/흐리게
            val (scale, alpha, saturation) = itemTransformByCenter(listState, resId)

            val matrix = remember(saturation) {
                ColorMatrix().apply { setToSaturation(saturation) }
            }

            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(matrix),
                modifier = Modifier
                    .size(153.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }
    }
}

/**
 * 중앙에 가까울수록:
 *  - scale: 1.00
 *  - alpha: 1.00
 *  - saturation: 1.00 (컬러)
 *
 * 멀어질수록:
 *  - scale: 0.92
 *  - alpha: 0.55
 *  - saturation: 0.00 (흑백)
 */
private fun itemTransformByCenter(
    listState: LazyListState,
    key: Int
): Triple<Float, Float, Float> {
    val layout = listState.layoutInfo
    val visible = layout.visibleItemsInfo
    if (visible.isEmpty()) return Triple(1f, 1f, 1f)

    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
    val item = visible.firstOrNull { it.key == key } ?: return Triple(0.92f, 0.55f, 0f)

    val itemCenter = item.offset + item.size / 2
    val distancePx = abs(itemCenter - viewportCenter).toFloat()
    val maxDistance = (layout.viewportEndOffset - layout.viewportStartOffset).toFloat() / 2f

    val t = (1f - (distancePx / maxDistance)).coerceIn(0f, 1f)

    val scale = lerp(0.92f, 1.00f, t)
    val alpha = lerp(0.55f, 1.00f, t)
    val saturation = lerp(0.00f, 1.00f, t)

    return Triple(scale, alpha, saturation)
}

private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

/** LazyRow에서 중앙에 가장 가까운 아이템 인덱스 추정 */
private fun LazyListState.centerItemIndex(): Int {
    val layout = layoutInfo
    val visible = layout.visibleItemsInfo
    if (visible.isEmpty()) return 0

    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
    val closest = visible.minByOrNull { item ->
        val itemCenter = item.offset + item.size / 2
        abs(itemCenter - viewportCenter)
    }
    return closest?.index ?: 0
}

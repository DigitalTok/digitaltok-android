package com.yourcompany.digitaltok.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicator(
    total: Int,
    selected: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(336.dp)     // Figma: 전체 인디케이터 영역 너비
            .height(8.dp),     // Figma: 높이
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,              // 도트 사이 간격 8dp
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.Top
    ) {
        repeat(total) { index ->
            if (index == selected) {
                // 선택된 인디케이터 (길게 24x8)
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(8.dp)
                        .background(
                            color = Color(0xFF155DFC),
                            shape = CircleShape
                        )
                )
            } else {
                // 비선택 인디케이터 (8x8 동그라미)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color(0xFFD1D5DB),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

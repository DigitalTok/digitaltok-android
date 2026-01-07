package com.yourcompany.digitaltok.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(total) { index ->
            if (index == selected) {
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

package com.yourcompany.digitaltok.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SkipButton(
    modifier: Modifier = Modifier,
    onSkip: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onSkip
    ) {
        Text(
            text = "건너뛰기",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4A5565)
        )
    }
}

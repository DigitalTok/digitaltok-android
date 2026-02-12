package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class OnboardingImageLayer(
    val resId: Int,
    val size: Dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp
)

data class OnboardingPageData(
    val title: String,
    val subtitle: String,


    val singleImageRes: Int? = null,
    val singleImageSize: Dp = 160.dp,


    val layers: List<OnboardingImageLayer> = emptyList(),


    val showSubwayCards: Boolean = false
)

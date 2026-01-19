package com.yourcompany.digitaltok.ui.onboarding

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val imageRes: Int? = null,     // 일반 아이콘 페이지면 drawable id
    val showSubwayCards: Boolean = false
)

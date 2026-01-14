package com.yourcompany.digitaltok.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yourcompany.digitaltok.R

enum class HomeTab {
    HOME, FAVORITE, DEVICE, SETTINGS
}

@Composable
fun BottomNavBar(
    selected: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    // 바깥 배경은 회색, 안쪽 네비바는 400dp 고정 폭
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F6)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    iconRes = R.drawable.ic_tab_home,
                    tab = HomeTab.HOME,
                    selected = selected,
                    onTabSelected = onTabSelected
                )
                NavItem(
                    iconRes = R.drawable.ic_tab_favorite,
                    tab = HomeTab.FAVORITE,
                    selected = selected,
                    onTabSelected = onTabSelected
                )
                NavItem(
                    iconRes = R.drawable.ic_tab_device,
                    tab = HomeTab.DEVICE,
                    selected = selected,
                    onTabSelected = onTabSelected
                )
                NavItem(
                    iconRes = R.drawable.ic_tab_settings,
                    tab = HomeTab.SETTINGS,
                    selected = selected,
                    onTabSelected = onTabSelected
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    iconRes: Int,
    tab: HomeTab,
    selected: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clickable { onTabSelected(tab) },
        contentAlignment = Alignment.Center
    ) {
        // PNG 아이콘 그대로 사용 (tint 안 씀)
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(if (tab == selected) 24.dp else 22.dp)
        )
    }
}

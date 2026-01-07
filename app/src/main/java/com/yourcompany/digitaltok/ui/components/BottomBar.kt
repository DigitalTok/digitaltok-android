package com.yourcompany.digitaltok.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.yourcompany.digitaltok.R

enum class HomeTab {
    HOME, FAVORITE, DEVICE, SETTINGS
}

@Composable
fun EmojiBottomBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiTabItem(
                iconRes = R.drawable.ic_tab_home,
                tab = HomeTab.HOME,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
            EmojiTabItem(
                iconRes = R.drawable.ic_tab_favorite,
                tab = HomeTab.FAVORITE,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
            EmojiTabItem(
                iconRes = R.drawable.ic_tab_device,
                tab = HomeTab.DEVICE,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
            EmojiTabItem(
                iconRes = R.drawable.ic_tab_settings,
                tab = HomeTab.SETTINGS,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
private fun EmojiTabItem(
    iconRes: Int,
    tab: HomeTab,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val isSelected = tab == selectedTab
    val color =
        if (isSelected) Color(0xFF155DFC)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .size(40.dp)
            .clickable { onTabSelected(tab) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        // 필요하면 아래에 작은 점 / 텍스트도 추가할 수 있음
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "●",
                fontSize = 10.sp,
                color = color
            )
        }
    }
}

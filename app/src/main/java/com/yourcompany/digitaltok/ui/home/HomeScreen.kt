package com.yourcompany.digitaltok.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.components.BottomNavBar

private object Variables {
    val Gray1 = Color(0xFFA0A0A0)
    val Point = Color(0xFF3AADFF)
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeTab() }
            composable("device") { CenterText("기기 연결") }
            composable("decorate") { CenterText("꾸미기") }
            composable("settings") { CenterText("설정") }
        }
    }
}

@Composable
private fun HomeTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(Modifier.height(18.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "당신만의 그립톡",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight(500),
                    color = Variables.Gray1
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "DigitalTok",
                style = TextStyle(
                    fontSize = 26.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF121212)
                )
            )
        }

        Spacer(Modifier.height(14.dp))

        // 회색 바(디자인용)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(Color(0xFFD9D9D9))
        )

        Spacer(Modifier.height(24.dp))

        // 중앙 카드(이미지는 네가 최종에서 연결)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .shadow(20.dp, RoundedCornerShape(10.dp))
                    .size(288.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF4F4F4), RoundedCornerShape(10.dp))
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "터치하여 이미지 변경",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 22.4.sp,
                fontWeight = FontWeight(600),
                color = Variables.Point
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun CenterText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

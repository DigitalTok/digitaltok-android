package com.yourcompany.digitaltok.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun AuthStartScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignupClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Di - ring",
            style = TextStyle(
                fontSize = 42.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF111111),
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "UX라이팅 문구 디링~",
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF505050),
                textAlign = TextAlign.Center
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        // 이메일 입력 박스(디자인용 더미)
        Box(
            modifier = Modifier
                .width(324.dp)
                .height(44.dp)
                .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                .padding(start = 16.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "이메일을 입력하세요 (예. diring@gmail.com)",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFA0A0A0)
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        // 비밀번호 입력 박스(디자인용 더미)
        Row(
            modifier = Modifier
                .width(324.dp)
                .height(44.dp)
                .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                .padding(start = 16.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "비밀번호를 입력하세요",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFA0A0A0)
                )
            )
            Image(
                painter = painterResource(id = R.drawable.eye_closed),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        // ✅ 피그마 규격 로그인 버튼(324x48, radius 4, padding 12/16)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .width(324.dp)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF36ABFF))
        ) {
            Text(
                text = "로그인",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight(700),
                    color = Color.White
                )
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.width(324.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
            Text(
                text = "또는",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFA0A0A0)
                )
            )
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = onSignupClick,
            modifier = Modifier
                .width(324.dp)
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111))
        ) {
            Text(
                text = "회원가입",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight(500),
                    color = Color.White
                )
            )
        }
    }
}

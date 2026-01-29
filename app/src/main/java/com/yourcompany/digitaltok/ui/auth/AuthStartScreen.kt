package com.yourcompany.digitaltok.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R

@Composable
fun AuthStartScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},   // ✅ 너 기존 네비게이션 그대로 유지
    onSignupClick: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }

    val isLoginEnabled = email.trim().isNotEmpty() && password.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Image(
            painter = painterResource(id = R.drawable.splash_full),
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

        // ✅ 이메일 입력 (진짜 입력)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .width(324.dp)
                .height(44.dp),
            placeholder = {
                Text(
                    text = "이메일을 입력하세요 (예. diring@gmail.com)",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFA0A0A0)
                    )
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F4F4),
                unfocusedContainerColor = Color(0xFFF4F4F4),
                disabledContainerColor = Color(0xFFF4F4F4),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF36ABFF)
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF111111)
            )
        )

        Spacer(Modifier.height(12.dp))

        // ✅ 비밀번호 입력 + 아이콘 클릭으로 보기/숨김 토글
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .width(324.dp)
                .height(44.dp),
            placeholder = {
                Text(
                    text = "비밀번호를 입력하세요",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFA0A0A0)
                    )
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // ✅ 너 프로젝트에 있는 eye_closed만 사용 (에러 방지)
                Image(
                    painter = painterResource(id = R.drawable.eye_closed),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { pwVisible = !pwVisible }
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F4F4),
                unfocusedContainerColor = Color(0xFFF4F4F4),
                disabledContainerColor = Color(0xFFF4F4F4),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF36ABFF)
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF111111)
            )
        )

        Spacer(Modifier.height(12.dp))

        // ✅ 로그인 버튼: 둘 다 입력되면 파란색
        Button(
            onClick = onLoginClick,
            enabled = isLoginEnabled,
            modifier = Modifier
                .width(324.dp)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoginEnabled) Color(0xFF36ABFF) else Color(0xFFE9E9E9),
                contentColor = if (isLoginEnabled) Color.White else Color(0xFF9E9E9E),
                disabledContainerColor = Color(0xFFE9E9E9),
                disabledContentColor = Color(0xFF9E9E9E)
            )
        ) {
            Text(
                text = "로그인",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,    // ✅ 텍스트 깨짐 완화
                    fontWeight = FontWeight(700),
                    letterSpacing = 0.sp,
                    color = if (isLoginEnabled) Color.White else Color(0xFF9E9E9E)
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
                    letterSpacing = 0.sp,
                    color = Color.White
                )
            )
        }
    }
}

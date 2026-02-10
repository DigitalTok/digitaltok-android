package com.yourcompany.digitaltok

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.auth.AuthStartScreen
import com.yourcompany.digitaltok.ui.auth.EmailLoginActivity
import com.yourcompany.digitaltok.ui.auth.SignupActivity
import com.yourcompany.digitaltok.ui.device.NfcViewModel
import com.yourcompany.digitaltok.ui.home.HomeScreen
import com.yourcompany.digitaltok.ui.onboarding.OnboardingScreen
import com.yourcompany.digitaltok.ui.theme.DigitalTokTheme
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()

    // ✅ 로그인 성공 여부를 Activity 쪽 상태로 들고 있다가 Compose로 전달
    private var goHomeState by mutableStateOf(false)

    // ✅ EmailLoginActivity 결과를 받아서 "로그인 성공" 확인 + 홈 이동 트리거
    private val emailLoginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val loginSuccess = result.data?.getBooleanExtra("login_success", false) ?: false
                if (loginSuccess) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    goHomeState = true
                }
            }
        }

    // (선택) 회원가입 완료 후 돌아오면 토스트/홈이동 등을 하고 싶으면 여기서 처리 가능
    private val signupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 예: 회원가입 완료 후 처리
                // Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 기존 intent extra도 유지 (예전에 go_home으로 홈 띄우던 로직)
        val goHomeFromIntent = intent.getBooleanExtra("go_home", false)
        goHomeState = goHomeFromIntent

        setContent {
            DigitalTokTheme {
                AppEntry(
                    goHome = goHomeState,
                    onOpenEmailLogin = {
                        // ✅ 로그인 화면(EmailLoginActivity) 열기
                        emailLoginLauncher.launch(Intent(this, EmailLoginActivity::class.java))
                    },
                    onOpenSignUp = {
                        signupLauncher.launch(Intent(this, SignupActivity::class.java))
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TAG_DISCOVERED
        ) {
            Log.d("NFC", "NFC Tag Intent received")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                nfcViewModel.onTagDiscovered(tag)
            }
        }
    }
}

@Composable
private fun AppEntry(
    goHome: Boolean,
    onOpenEmailLogin: () -> Unit,
    onOpenSignUp: () -> Unit
) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1200)
        showSplash = false
    }

    if (showSplash) {
        SplashImage()
    } else {
        AppNavHost(
            goHome = goHome,
            onOpenEmailLogin = onOpenEmailLogin,
            onOpenSignUp = onOpenSignUp
        )
    }
}

@Composable
private fun SplashImage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_full),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun AppNavHost(
    goHome: Boolean,
    navController: NavHostController = rememberNavController(),
    onOpenEmailLogin: () -> Unit = {},
    onOpenSignUp: () -> Unit = {}
) {
    // ✅ goHome가 true가 되는 순간(=로그인 성공) home으로 이동
    LaunchedEffect(goHome) {
        if (goHome) {
            navController.navigate("home") {
                // 로그인 성공 후 홈만 남기고 스택 정리(원하는 방식으로 조절 가능)
                popUpTo("onboarding") { inclusive = true }
            }
        }
    }

    NavHost(navController, startDestination = "onboarding") {

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("signup") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            AuthStartScreen(
                // ✅ 기존엔 여기서 home으로 바로 갔는데, 이제는 "로그인 화면 열기"로 바꿈
                onLoginClick = { onOpenEmailLogin() },
                onSignupClick = { onOpenSignUp() }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}

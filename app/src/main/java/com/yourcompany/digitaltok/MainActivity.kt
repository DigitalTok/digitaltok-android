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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.auth.AuthStartScreen
import com.yourcompany.digitaltok.ui.auth.EmailLoginActivity
import com.yourcompany.digitaltok.ui.auth.SignupActivity
import com.yourcompany.digitaltok.ui.device.NfcViewModel
import com.yourcompany.digitaltok.ui.home.HomeScreen
import com.yourcompany.digitaltok.ui.onboarding.OnboardingScreen
import com.yourcompany.digitaltok.ui.theme.DigitalTokTheme
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : AppCompatActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    // 로그인 성공 여부를 Activity 쪽 상태로 들고 있다가 Compose로 전달
    private var goHomeState by mutableStateOf(false)

    // EmailLoginActivity 결과를 받아서 "로그인 성공" 확인 + 홈 이동 트리거
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

    private val signupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 회원가입 완료 후 처리 필요하면 여기서
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        enableEdgeToEdge()
        // 기존 intent extra도 유지 (예전에 go_home으로 홈 띄우던 로직)
        val goHomeFromIntent = intent.getBooleanExtra("go_home", false)
        goHomeState = goHomeFromIntent

        setContent {
            DigitalTokTheme {
                AppEntry(
                    mainViewModel = mainViewModel,
                    goHome = goHomeState,
                    onOpenEmailLogin = {
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
    mainViewModel: MainViewModel,
    goHome: Boolean,
    onOpenEmailLogin: () -> Unit,
    onOpenSignUp: () -> Unit
) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1800)
        showSplash = false
    }

    if (showSplash) {
        SplashLanding()
    } else {
        AppNavHost(
            mainViewModel = mainViewModel,
            goHome = goHome,
            onOpenEmailLogin = onOpenEmailLogin,
            onOpenSignUp = onOpenSignUp
        )
    }
}

@Composable
private fun SplashLanding() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.diringlogo),
                contentDescription = null,
                modifier = Modifier.size(width = 48.dp, height = 75.dp)
            )

            Spacer(modifier = Modifier.height(37.dp))

            Text(
                text = "DiRing",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "내 마음대로 꾸미는 나만의 키링",
                fontSize = 20.sp,
                color = Color(0xFF6B6B6B)
            )
        }
    }
}

@Composable
fun AppNavHost(
    mainViewModel: MainViewModel,
    goHome: Boolean,
    navController: NavHostController = rememberNavController(),
    onOpenEmailLogin: () -> Unit = {},
    onOpenSignUp: () -> Unit = {}
) {
    // goHome가 true가 되는 순간(=로그인 성공) home으로 이동
    LaunchedEffect(goHome) {
        if (goHome) {
            navController.navigate("home") {
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
                onLoginClick = { onOpenEmailLogin() },
                onSignupClick = { onOpenSignUp() }
            )
        }

        composable("home") {
            HomeScreen(mainViewModel = mainViewModel)
        }
    }
}

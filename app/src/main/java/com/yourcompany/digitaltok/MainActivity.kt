package com.yourcompany.digitaltok

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val goHome = intent.getBooleanExtra("go_home", false)

        setContent {
            DigitalTokTheme {
                AppEntry(
                    goHome = goHome,
                    onOpenEmailLogin = {
                        startActivity(Intent(this, EmailLoginActivity::class.java))
                    },
                    onOpenSignUp = {
                        startActivity(Intent(this, SignupActivity::class.java))
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
        delay(1200) // ⏱ 보여줄 시간
        showSplash = false
    }

    if (showSplash) {
        SplashImage()
    } else {
        AppNavHost(
            startDestination = if (goHome) "home" else "onboarding",
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
    navController: NavHostController = rememberNavController(),
    startDestination: String = "onboarding",
    onOpenEmailLogin: () -> Unit = {},
    onOpenSignUp: () -> Unit = {}
) {
    NavHost(navController, startDestination = startDestination) {

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
                onLoginClick = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onSignupClick = { onOpenSignUp() }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}

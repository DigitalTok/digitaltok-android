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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.auth.AuthStartScreen
import com.yourcompany.digitaltok.ui.device.NfcViewModel
import com.yourcompany.digitaltok.ui.home.HomeScreen
import com.yourcompany.digitaltok.ui.onboarding.OnboardingScreen
import com.yourcompany.digitaltok.ui.theme.DigitalTokTheme

class MainActivity : AppCompatActivity() {

    // 프래그먼트와 NFC 태그 정보를 공유하기 위한 ViewModel
    private val nfcViewModel: NfcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DigitalTokTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }

    /**
     * NFC 태그가 감지되었을 때 시스템에 의해 호출되는 메서드
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // NFC 태그 인텐트인지 확인
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED || intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            Log.d("NFC", "NFC Tag Intent received in MainActivity")
            // Intent에서 Tag 객체를 추출
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                // 추출한 Tag 객체를 ViewModel에 전달
                nfcViewModel.onTagDiscovered(tag)
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
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
                onSignupSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}

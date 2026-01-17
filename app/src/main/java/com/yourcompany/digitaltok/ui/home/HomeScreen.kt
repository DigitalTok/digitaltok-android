package com.yourcompany.digitaltok.ui.home

import android.view.View
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.components.BottomNavBar
import com.yourcompany.digitaltok.ui.decorate.DecorateFragment
import com.yourcompany.digitaltok.ui.device.DeviceConnectFragment
import com.yourcompany.digitaltok.ui.faq.HelpFragment

private object Variables {
    val Gray1 = Color(0xFFA0A0A0)
    val Point = Color(0xFF3AADFF)
}

// Fragment를 Compose에서 표시하는 컴포저블 함수
@Composable
private fun ComposableFragmentContainer(modifier: Modifier = Modifier, fragment: () -> Fragment) {
    val containerId = remember { View.generateViewId() }
    val context = LocalContext.current
    AndroidView(
        factory = {
            FragmentContainerView(it).apply { id = containerId }
        },
        modifier = modifier,
        update = {
            val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
            if (fragmentManager != null && fragmentManager.findFragmentById(containerId) == null) {
                fragmentManager.commit {
                    setReorderingAllowed(true)
                    add(containerId, fragment())
                }
            }
        }
    )
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
            composable("device") {
                val context = LocalContext.current
                val activity = context as FragmentActivity
                val mainViewModel: MainViewModel = viewModel(viewModelStoreOwner = activity)
                val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

                if (isDeviceConnected) {
                    // 기기가 이미 연결된 경우, 토스트 메시지를 보여주고 홈 탭 내용을 표시
                    HomeTab()
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "이미 기기가 연결되어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 기기가 연결되지 않은 경우, 연결 프래그먼트를 표시
                    val fragmentManager = activity.supportFragmentManager
                    val backStackEntryCount by produceState(
                        initialValue = fragmentManager.backStackEntryCount,
                        key1 = fragmentManager
                    ) {
                        val listener = FragmentManager.OnBackStackChangedListener {
                            value = fragmentManager.backStackEntryCount
                        }
                        fragmentManager.addOnBackStackChangedListener(listener)
                        awaitDispose {
                            fragmentManager.removeOnBackStackChangedListener(listener)
                        }
                    }

                    BackHandler(enabled = backStackEntryCount > 0) {
                        fragmentManager.popBackStack()
                    }

                    ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) {
                        DeviceConnectFragment()
                    }
                }
            }

            composable("decorate") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) {
                    DecorateFragment()
                }
            }

            composable("settings") {
                val context = LocalContext.current
                val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

                val backStackEntryCount by produceState(
                    initialValue = fragmentManager?.backStackEntryCount ?: 0,
                    key1 = fragmentManager
                ) {
                    val listener = FragmentManager.OnBackStackChangedListener {
                        value = fragmentManager?.backStackEntryCount ?: 0
                    }
                    fragmentManager?.addOnBackStackChangedListener(listener)
                    awaitDispose {
                        fragmentManager?.removeOnBackStackChangedListener(listener)
                    }
                }

                BackHandler(enabled = backStackEntryCount > 0) {
                    fragmentManager?.popBackStack()
                }

                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) {
                    HelpFragment()
                }
            }
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(Color(0xFFD9D9D9))
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
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

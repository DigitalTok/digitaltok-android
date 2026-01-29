package com.yourcompany.digitaltok.ui.home

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.components.BottomNavBar
import com.yourcompany.digitaltok.ui.decorate.DecorateFragment
import com.yourcompany.digitaltok.ui.device.DeviceConnectFragment
import com.yourcompany.digitaltok.ui.faq.HelpFragment

private object Variables {
    val Gray1 = Color(0xFFA0A0A0)
    val Point = Color(0xFF3AADFF)
}

@Composable
private fun ComposableFragmentContainer(modifier: Modifier = Modifier, fragment: () -> Fragment) {
    val containerId = remember { View.generateViewId() }
    val context = LocalContext.current

    AndroidView(
        factory = { FragmentContainerView(it).apply { id = containerId } },
        modifier = modifier,
        update = {
            val fm = (context as? FragmentActivity)?.supportFragmentManager
            if (fm != null && fm.findFragmentById(containerId) == null) {
                fm.commit {
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
            modifier = Modifier.padding(
                start = 0.dp,
                end = 0.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            composable("home") { HomeTab() }

            composable("device") {
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
                    awaitDispose { fragmentManager?.removeOnBackStackChangedListener(listener) }
                }

                BackHandler(enabled = backStackEntryCount > 0) {
                    fragmentManager?.popBackStack()
                }

                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { DeviceConnectFragment() }
            }

            composable("decorate") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { DecorateFragment() }
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
                    awaitDispose { fragmentManager?.removeOnBackStackChangedListener(listener) }
                }

                BackHandler(enabled = backStackEntryCount > 0) {
                    fragmentManager?.popBackStack()
                }

                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { HelpFragment() }
            }
        }
    }
}

@Composable
private fun HomeTab() {
    // ✅ develop 기준: MainViewModel에서 상태를 가져와 홈 화면 분기
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val mainViewModel: MainViewModel = viewModel(viewModelStoreOwner = activity)
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

    if (!isDeviceConnected) {
        HomeNoConnection()
    } else {
        HomeConnected()
    }
}

@Composable
private fun HomeNoConnection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(Modifier.height(18.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "당신만의 그~~",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = Variables.Gray1
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Di-ring",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF121212)
                )
            )
        }

        Spacer(Modifier.height(110.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_no_connection),
                contentDescription = null,
                modifier = Modifier.size(width = 198.dp, height = 156.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "아직 연결된 기기가 없습니다",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFA0A0A0)
                )
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "기기 연결",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(700),
                    color = Variables.Point
                )
            )
            Text(
                text = "에서\n디지털톡을 찾아주세요",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600),
                    color = Variables.Gray1,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "▼",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(800),
                    color = Variables.Point
                )
            )
        }
    }
}

@Composable
private fun HomeConnected() {
    val imageRes = R.drawable.rectangle_95

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
                    fontWeight = FontWeight(500),
                    color = Variables.Gray1
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "DigitalTok",
                style = TextStyle(
                    fontSize = 26.sp,
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
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .shadow(20.dp, RoundedCornerShape(12.dp))
                    .size(288.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF4F4F4))
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "터치하여 이미지 변경",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Variables.Point
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

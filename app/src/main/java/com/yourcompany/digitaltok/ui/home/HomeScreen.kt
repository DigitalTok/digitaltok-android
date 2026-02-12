package com.yourcompany.digitaltok.ui.home

import android.view.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.fragment.app.commit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.MainUiViewModel
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
fun HomeScreen(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as FragmentActivity

    // 탭 이동 이벤트는 MainUiViewModel
    val mainUiViewModel: MainUiViewModel = viewModel(viewModelStoreOwner = activity)

    // 하단바 가시성 / 연결 상태는 MainViewModel (단일 소스)
    val isBottomNavVisible by mainViewModel.isBottomNavVisible.observeAsState(initial = true)
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

    // Fragment에서 요청한 탭 이동 처리
    val navigateTo by mainUiViewModel.navigateTo.collectAsState()
    LaunchedEffect(navigateTo) {
        navigateTo?.let { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
            mainUiViewModel.consumeNavigate()
        }
    }

    Scaffold(
        bottomBar = {
            if (isBottomNavVisible) {
                BottomNavBar(navController = navController, onItemClick = { route ->
                    if (route == "device" && isDeviceConnected) {
                        Toast.makeText(context, "이미 기기가 연결되어 있습니다.", Toast.LENGTH_SHORT).show()
                        false
                    } else true
                })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(
                start = 0.dp,
                end = 0.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = if (isBottomNavVisible) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable("home") { HomeTab(mainViewModel) }
            composable("home") {
                HomeTab(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }


            composable("device") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { DeviceConnectFragment() }
            }

            composable("decorate") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { DecorateFragment() }
            }

            composable("settings") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { HelpFragment() }
            }
        }
    }
}

@Composable
private fun HomeTab(mainViewModel: MainViewModel, navController: NavHostController) {
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

    if (!isDeviceConnected) {
        HomeNoConnection()
    } else {
        HomeConnected(
            mainViewModel = mainViewModel,
            navController = navController
        )
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
private fun HomeConnected(mainViewModel: MainViewModel, navController: NavController) {
    val lastImageUrl by mainViewModel.lastTransferredImageUrl.observeAsState()

    val navigateToDecorate = {
        navController.navigate("decorate")
    }

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
                    .clickable { navigateToDecorate() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF4F4F4))
                ) {
                    AsyncImage(
                        model = lastImageUrl ?: R.drawable.rectangle_95,
                        contentDescription = "Last transferred image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.rectangle_95),
                        placeholder = painterResource(id = R.drawable.rectangle_95)
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
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { navigateToDecorate() }
        )
    }
}

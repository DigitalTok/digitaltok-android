package com.yourcompany.digitaltok.ui.home

import android.view.View
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
private fun ComposableFragmentContainer(
    modifier: Modifier = Modifier,
    fragment: () -> Fragment
) {
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

    val mainUiViewModel: MainUiViewModel = viewModel(viewModelStoreOwner = activity)

    val isBottomNavVisible by mainViewModel.isBottomNavVisible.observeAsState(initial = true)
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

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
                BottomNavBar(
                    navController = navController,
                    onItemClick = { route ->
                        if (route == "device" && isDeviceConnected) {
                            Toast.makeText(context, "이미 기기가 연결되어 있습니다.", Toast.LENGTH_SHORT).show()
                            false
                        } else true
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = if (isBottomNavVisible) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable("home") { HomeTab(mainViewModel) }

            composable("device") {
                ComposableFragmentContainer(Modifier.fillMaxSize()) {
                    DeviceConnectFragment()
                }
            }

            composable("decorate") {
                // ✅ DecorateFragment 내부에서 Fragment backstack을 쌓는다면, 뒤로가기 처리 필요
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

                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) {
                    DecorateFragment()
                }
            }

            composable("settings") {
                ComposableFragmentContainer(Modifier.fillMaxSize()) {
                    HelpFragment()
                }
            }
        }
    }
}

@Composable
private fun HomeTab(mainViewModel: MainViewModel) {
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

    if (!isDeviceConnected) {
        HomeNoConnection()
    } else {
        HomeConnected()
    }
}

@Composable
private fun HomeNoConnection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Di-ring",
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF121212)
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 18.dp, start = 16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_no_connection),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 40.dp)
                .size(width = 198.dp, height = 156.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Box(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

            Box(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HomeConnected() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(100.dp))
        Text("연결된 상태 화면")
    }
}

// refresh PR for home file

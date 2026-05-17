package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.screens.AddAccountScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.SearchScreen
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 主 Activity - 配置 Navigation Compose 导航和所有过渡动画
 * 
 * 动画说明：
 * 1. 主页 → 搜索页：Container Transform（搜索栏展开到全屏）
 *    参考：https://m3.material.io/styles/motion/transitions/transition-patterns#425522a0-60f9-4ae0-ba7b-4617f0e40e64
 * 2. 主页 → 设置页：Shared Axis X（水平共享轴过渡）
 *    参考：https://m3.material.io/styles/motion/transitions/transition-patterns#df9c7d76-1454-47f3-ad1c-268a31f58bad
 * 3. 主页 → 添加账户页：Container Transform（FAB展开到全屏）
 *    参考：https://m3.material.io/styles/motion/transitions/transition-patterns#b67cba74-6240-4663-a423-d537b6d21187
 * 4. 菜单键 → 返回键：Shared Axis 设计（Google 推荐的导航图标动画）
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AuthenticatorApp()
            }
        }
    }
}

@Composable
fun AuthenticatorApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // ==================== 主页 ====================
        composable(
            route = Screen.Home.route,
            // 主页使用淡入淡出作为默认动画
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            HomeScreen(
                onMenuClick = {
                    // 菜单键点击 → 导航到设置页（原生页面跳转）
                    // 使用 Shared Axis X 动画
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                },
                onSearchClick = {
                    // 搜索栏点击 → 导航到搜索页（Container Transform 动画）
                    navController.navigate(Screen.Search.route) {
                        launchSingleTop = true
                    }
                },
                onFabClick = {
                    // FAB 点击 → 导航到添加账户页（Container Transform 动画）
                    navController.navigate(Screen.AddAccount.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ==================== 搜索页 ====================
        // Container Transform：搜索栏 → 全屏搜索视图
        // 参考：https://m3.material.io/styles/motion/transitions/transition-patterns#425522a0-60f9-4ae0-ba7b-4617f0e40e64
        composable(
            route = Screen.Search.route,
            enterTransition = {
                // Container Transform 进入动画：从中心展开 + 淡入
                // 模拟搜索栏从小尺寸放大到全屏的效果
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ) + scaleIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    ),
                    initialScale = 0.92f
                )
            },
            exitTransition = {
                // Container Transform 退出动画：缩小 + 淡出
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    ),
                    targetScale = 0.92f
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(300)
                ) + scaleIn(
                    animationSpec = tween(300),
                    initialScale = 0.92f
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(250)
                ) + scaleOut(
                    animationSpec = tween(250),
                    targetScale = 0.92f
                )
            }
        ) {
            SearchScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ==================== 设置页 ====================
        // Shared Axis X 动画：水平共享轴过渡
        // 参考：https://m3.material.io/styles/motion/transitions/transition-patterns#df9c7d76-1454-47f3-ad1c-268a31f58bad
        // 主页向左滑出，设置页从右侧滑入（Forward）
        // 设置页向右滑出，主页从左侧滑入（Backward）
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                // Shared Axis X Forward：新页面从右侧滑入
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                // Shared Axis X Forward：旧页面向左滑出
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                // Shared Axis X Backward：主页从左侧滑入
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                // Shared Axis X Backward：设置页向右滑出
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            }
        ) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ==================== 添加账户页 ====================
        // Container Transform：FAB (+) → 全屏添加账户页面
        // 参考：https://m3.material.io/styles/motion/transitions/transition-patterns#b67cba74-6240-4663-a423-d537b6d21187
        composable(
            route = Screen.AddAccount.route,
            enterTransition = {
                // Container Transform 进入动画：从右下角（FAB位置）展开
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = LinearOutSlowInEasing
                    )
                ) + scaleIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = LinearOutSlowInEasing
                    ),
                    initialScale = 0.8f,
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 1f)
                )
            },
            exitTransition = {
                // Container Transform 退出动画：缩小回右下角
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    ),
                    targetScale = 0.8f,
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 1f)
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(350)
                ) + scaleIn(
                    animationSpec = tween(350),
                    initialScale = 0.8f,
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 1f)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(250)
                ) + scaleOut(
                    animationSpec = tween(250),
                    targetScale = 0.8f,
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 1f)
                )
            }
        ) {
            AddAccountScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
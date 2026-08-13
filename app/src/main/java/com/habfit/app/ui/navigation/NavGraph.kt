package com.habfit.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.features.auth.LoginScreen
import com.habfit.app.features.auth.SignupScreen
import com.habfit.app.features.main.MainScreen
import com.habfit.app.features.onboarding.OnboardingScreen
import com.habfit.app.features.splash.SplashScreen
import com.habfit.app.features.splash.SplashViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Main : Screen("main")
    object AI : Screen("ai")
    object Home : Screen("home")
    object Habits : Screen("habits")
    object Fitness : Screen("fitness")
    object Community : Screen("community")
    object Profile : Screen("profile")
}

@Composable
fun HabfitNavGraph(
    navController: NavHostController,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onAnimationFinished = {
                val destination = splashViewModel.getStartDestination()
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onSignupSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}

package com.habfit.app.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habfit.app.features.ai.AIScreen
import com.habfit.app.features.community.CommunityScreen
import com.habfit.app.features.fitness.FitnessScreen
import com.habfit.app.features.habits.HabitScreen
import com.habfit.app.features.home.HomeScreen
import com.habfit.app.features.profile.ProfileScreen
import com.habfit.app.ui.navigation.HabfitBottomNavigationBar
import com.habfit.app.ui.navigation.Screen
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.PurpleAI

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Background,
        bottomBar = {
            HabfitBottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AI.route) },
                containerColor = PurpleAI,
                contentColor = PrimaryText,
                shape = CircleShape
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Coach")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Habits.route) { HabitScreen() }
            composable(Screen.AI.route) { AIScreen() }
            composable(Screen.Fitness.route) { FitnessScreen() }
            composable(Screen.Community.route) { CommunityScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}

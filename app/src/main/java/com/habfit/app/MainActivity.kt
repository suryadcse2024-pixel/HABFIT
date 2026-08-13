package com.habfit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.habfit.app.ui.navigation.HabfitNavGraph
import com.habfit.app.ui.theme.HabfitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabfitTheme {
                val navController = rememberNavController()
                HabfitNavGraph(navController = navController)
            }
        }
    }
}

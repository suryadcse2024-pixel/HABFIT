package com.habfit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.habfit.app.sensors.StepSensorManager
import com.habfit.app.ui.navigation.HabfitNavGraph
import com.habfit.app.ui.theme.HabfitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var stepSensorManager: StepSensorManager

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

    override fun onResume() {
        super.onResume()
        stepSensorManager.startListening()
    }

    override fun onPause() {
        super.onPause()
        stepSensorManager.stopListening()
    }
}

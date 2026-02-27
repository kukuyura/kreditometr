package ru.kreditometr.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.kreditometr.app.ui.navigation.KreditometrNavHost
import ru.kreditometr.app.ui.state.SharedCalculationState
import ru.kreditometr.app.ui.theme.KreditometrTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KreditometrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val sharedState: SharedCalculationState = viewModel()
                    KreditometrNavHost(
                        navController = navController,
                        sharedState = sharedState
                    )
                }
            }
        }
    }
}

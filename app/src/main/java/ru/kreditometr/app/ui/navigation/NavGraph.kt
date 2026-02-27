package ru.kreditometr.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.kreditometr.app.ui.screens.InputScreen
import ru.kreditometr.app.ui.screens.ResultScreen
import ru.kreditometr.app.ui.screens.ScheduleFullScreen
import ru.kreditometr.app.ui.state.SharedCalculationState

const val ROUTE_INPUT = "input"
const val ROUTE_RESULT = "result"
const val ROUTE_SCHEDULE_FULL = "schedule_full"

@Composable
fun KreditometrNavHost(
    navController: NavHostController = rememberNavController(),
    sharedState: SharedCalculationState
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_INPUT
    ) {
        composable(ROUTE_INPUT) {
            InputScreen(
                sharedState = sharedState,
                onNavigateToResult = { navController.navigate(ROUTE_RESULT) }
            )
        }
        composable(ROUTE_RESULT) {
            ResultScreen(
                sharedState = sharedState,
                onBack = { navController.popBackStack() },
                onShowFullSchedule = { navController.navigate(ROUTE_SCHEDULE_FULL) }
            )
        }
        composable(ROUTE_SCHEDULE_FULL) {
            ScheduleFullScreen(
                sharedState = sharedState,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

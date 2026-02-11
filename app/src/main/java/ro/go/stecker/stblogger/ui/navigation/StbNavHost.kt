package ro.go.stecker.stblogger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ro.go.stecker.stblogger.ui.AppViewModelProvider
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.screens.TripsScreen

enum class StbScreen {
    TripsScreen
}

@Composable
fun StbNavHost(
    navController: NavHostController,
    viewModel: StbViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = StbScreen.TripsScreen.name
    ) {
        composable(
            route = StbScreen.TripsScreen.name
        ) {
            TripsScreen(
                viewModel = viewModel,
                uiState = uiState
            )
        }
    }
}
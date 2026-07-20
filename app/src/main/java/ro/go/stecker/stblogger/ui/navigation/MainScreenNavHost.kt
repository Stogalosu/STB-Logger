package ro.go.stecker.stblogger.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState
import ro.go.stecker.stblogger.ui.screens.LinesScreen
import ro.go.stecker.stblogger.ui.screens.TripsScreen

enum class StbTab(name: String) {
    Trips("Trips"),
    Lines("Lines")
}

@Composable
fun MainScreenNavHost(
    navController: NavHostController,
    onInfoClick: (Int) -> Unit,
    fabHeight: Dp,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: StbViewModel
) {
    NavHost(
        navController = navController,
        startDestination = StbTab.Trips.name,
        enterTransition = {
            val initialIndex = StbTab.entries.indexOf(StbTab.entries.find { it.name == initialState.destination.route })
            val targetIndex = StbTab.entries.indexOf(StbTab.entries.find { it.name == targetState.destination.route })
            when(initialIndex < targetIndex) {
                true -> {
                    slideInHorizontally { it }
                }
                false -> {
                    slideInHorizontally { -it }
                }
            }
        },
        exitTransition = {
            val initialIndex = StbTab.entries.indexOf(StbTab.entries.find { it.name == initialState.destination.route })
            val targetIndex = StbTab.entries.indexOf(StbTab.entries.find { it.name == targetState.destination.route })
            when(initialIndex < targetIndex) {
                true -> {
                    slideOutHorizontally { -it }
                }
                false -> {
                    slideOutHorizontally { it }
                }
            }
        }
    ) {
        composable(route = StbTab.Trips.name) {
            TripsScreen(
                onInfoClick = onInfoClick,
                fabHeight = fabHeight,
                innerPadding = innerPadding,
                uiState = uiState,
                viewModel = viewModel
            )
        }

        composable(route = StbTab.Lines.name) {
            LinesScreen(
                innerPadding = innerPadding
            )
        }
    }
}
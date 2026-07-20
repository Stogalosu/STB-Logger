package ro.go.stecker.stblogger.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.distinctUntilChanged
import ro.go.stecker.stblogger.ui.AppViewModelProvider
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UpdateStatus
import ro.go.stecker.stblogger.ui.dialogs.NoInternetDialog
import ro.go.stecker.stblogger.ui.dialogs.UpdateDatabaseDialog
import ro.go.stecker.stblogger.ui.screens.MainScreen
import ro.go.stecker.stblogger.ui.screens.NewTripScreen
import ro.go.stecker.stblogger.ui.screens.TripInfoScreen

enum class StbScreen {
    MainScreen,
    TripInfoScreen,
    NewTripScreen,
    UpdateDatabasesDialog,
    NoInternetDialog
}

@Composable
fun StbNavHost(
    navController: NavHostController,
    viewModel: StbViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsState()
    var previousConnected by rememberSaveable { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snapshotFlow { uiState.isConnected }
            .distinctUntilChanged()
            .collect { value ->
                if(!value && previousConnected){
                    navController.navigate(StbScreen.NoInternetDialog.name)
                    previousConnected = false
                }
                else if(value && !previousConnected)
                    previousConnected = true
            }
    }

    LaunchedEffect(uiState.databaseUpdateStatus) {
        if(uiState.databaseUpdateStatus == UpdateStatus.Updating) {
            navController.navigate(StbScreen.UpdateDatabasesDialog.name)
        }
    }

    NavHost(
        navController = navController,
        startDestination = StbScreen.MainScreen.name,
        enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { -80 },
                animationSpec = tween()
            )
        },
        popEnterTransition = {
            slideInVertically(
                initialOffsetY = { -80 },
                animationSpec = tween(durationMillis = 150)
            )
        },
        popExitTransition = {
            slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(
                tween(
                    durationMillis = 200,
                    delayMillis = 100
                )
            )
        }
    ) {
        composable(route = StbScreen.MainScreen.name) {
            MainScreen(
                onInfoClick = { navController.navigate(StbScreen.TripInfoScreen.name + "/" + it) },
                onNewTripClick = { navController.navigate(StbScreen.NewTripScreen.name) },
                snackbarHostState = snackbarHostState,
                uiState = uiState,
                viewModel = viewModel
            )
        }

        composable(
            route = StbScreen.TripInfoScreen.name + "/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0

            TripInfoScreen(
                trip = uiState.trips.first { it.id == tripId },
                onNavigateBack = { navController.popBackStack() },
                uiState = uiState,
                viewModel = viewModel
            )
        }

        composable(route = StbScreen.NewTripScreen.name) {
            NewTripScreen(
                onNavigateBack = { navController.popBackStack() },
                uiState = uiState,
                viewModel = viewModel
            )
        }

        dialog(route = StbScreen.UpdateDatabasesDialog.name) {
            UpdateDatabaseDialog(
                onDismiss = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        dialog(route = StbScreen.NoInternetDialog.name) {
            NoInternetDialog(
                onDismissRequest = { navController.popBackStack() }
            )
        }
    }
}
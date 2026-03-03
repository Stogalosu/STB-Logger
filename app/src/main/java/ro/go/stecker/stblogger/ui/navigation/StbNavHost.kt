package ro.go.stecker.stblogger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import kotlinx.coroutines.flow.distinctUntilChanged
import ro.go.stecker.stblogger.ui.AppViewModelProvider
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.dialogs.NoInternetDialog
import ro.go.stecker.stblogger.ui.dialogs.UpdateStopDatabaseDialog
import ro.go.stecker.stblogger.ui.screens.TripsScreen

enum class StbScreen {
    TripsScreen,
    UpdateStopDatabaseDialog,
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

    NavHost(
        navController = navController,
        startDestination = StbScreen.TripsScreen.name
    ) {
        composable(
            route = StbScreen.TripsScreen.name
        ) {
            TripsScreen(
                viewModel = viewModel,
                showUpdateStopDataDialog = { navController.navigate(StbScreen.UpdateStopDatabaseDialog.name) },
                uiState = uiState
            )
        }

        dialog(route = StbScreen.UpdateStopDatabaseDialog.name) {
            UpdateStopDatabaseDialog(
                onDismissRequest = { navController.popBackStack() },
                updateDatabase = { onFinish -> viewModel.updateStopDatabase { onFinish() } },
                onNoInternet = { navController.navigate(StbScreen.NoInternetDialog.name) },
                uiState = uiState
            )
        }

        dialog(route = StbScreen.NoInternetDialog.name) {
            NoInternetDialog(
                onDismissRequest = { navController.popBackStack() }
            )
        }
    }
}
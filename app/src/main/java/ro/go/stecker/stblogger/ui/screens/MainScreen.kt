package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState
import ro.go.stecker.stblogger.ui.navigation.MainScreenNavHost
import ro.go.stecker.stblogger.ui.navigation.StbNavBar

@Composable
fun MainScreen(
    onInfoClick: (Int) -> Unit,
    onNewTripClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: StbViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    var fabHeight by remember { mutableStateOf(0.dp) }
    val navController = rememberNavController()

    val failedSearchText = stringResource(R.string.no_stop_line_found)

    Scaffold(
        topBar = {
            StbTopAppBar(
                canNavigateBack = false,
                canSearch = true,
                onSearchFail = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(failedSearchText)
                    }
                },
                uiState = uiState,
                viewModel = viewModel
            )
        },
        floatingActionButton = {
            val current = LocalDensity.current
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.new_trip)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.add_24px),
                        contentDescription = stringResource(R.string.new_trip)
                    )
                },
                onClick = onNewTripClick,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        fabHeight = with(current) { coordinates.size.height.toDp() }
                    }
            )
        },
        bottomBar = {
            StbNavBar(
                onScreenChange = { navController.navigate(it.name) },
                uiState = uiState
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        MainScreenNavHost(
            navController = navController,
            onInfoClick = onInfoClick,
            fabHeight = fabHeight,
            innerPadding = innerPadding,
            snackbarHostState = snackbarHostState,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}
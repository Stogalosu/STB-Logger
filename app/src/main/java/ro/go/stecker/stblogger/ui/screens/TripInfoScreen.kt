package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.UiState

@Composable
fun TripInfoScreen(
    trip: Trip,
    onNavigateBack: () -> Unit,
    uiState: UiState
) {
    Scaffold(
        topBar = {
            StbTopAppBar(
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                canSearch = false,
                uiState = uiState
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {paddingValues ->
        Box(Modifier.padding(paddingValues))





    }

}
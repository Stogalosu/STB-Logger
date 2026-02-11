package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.data.Trip
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    viewModel: StbViewModel,
    uiState: UiState
) {

    if(uiState.cannotUpdateTripsDialog) {
        AlertDialog(
            onDismissRequest = { uiState.cannotUpdateTripsDialog = false },
            icon = { },
            title = { Text(text = stringResource(R.string.stop_update_failed)) },
            text = { Text(text = stringResource(R.string.stop_update_failed_dialog)) },
            confirmButton = {
                TextButton(onClick = { uiState.cannotUpdateTripsDialog = false }) {
                    Text(text = stringResource(R.string.got_it))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            StbTopAppBar(
                canNavigateBack = false,
                canSearch = true,
                viewModel = viewModel
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            items(items = uiState.trips, key = { it.id }) { trip ->
                TripCard(trip, uiState)
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    uiState: UiState
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(text = "Id: "+trip.id.toString())
            Text(text = "Type: "+trip.type.name)
            Text(text = "StartId"+trip.startId.toString())
            Text(text = "EndId"+trip.endId.toString())
            Text(text = "asidjbas"+uiState.stops[0])
        }
    }
}
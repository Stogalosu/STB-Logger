package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ro.go.stecker.stblogger.data.Line
import ro.go.stecker.stblogger.data.Stop
import ro.go.stecker.stblogger.data.Trip
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState

val discardRed = Color(224, 65, 65)
val confirmGreen = Color(87, 201, 90)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    showUpdateStopDataDialog: () -> Unit,
    viewModel: StbViewModel,
    uiState: UiState
) {

//    if(uiState.cannotUpdateTripsDialog) {
//        AlertDialog(
//            onDismissRequest = { uiState.cannotUpdateTripsDialog = false },
//            icon = { },
//            title = { Text(text = stringResource(R.string.stop_update_failed)) },
//            text = { Text(text = stringResource(R.string.stop_update_failed_dialog)) },
//            confirmButton = {
//                TextButton(onClick = { uiState.cannotUpdateTripsDialog = false }) {
//                    Text(text = stringResource(R.string.got_it))
//                }
//            }
//        )
//    }


    Scaffold(
        topBar = {
            StbTopAppBar(
                canNavigateBack = false,
                canSearch = true,
                uiState = uiState,
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
                TripCard(trip, showUpdateStopDataDialog, uiState, viewModel)
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    showUpdateStopDataDialog: () -> Unit,
    uiState: UiState,
    viewModel: StbViewModel
) {
    var line by remember { mutableStateOf(Line()) }
    val stops = remember { mutableStateListOf(Stop(), Stop()) }
    var showedStopDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.updatedDatabase) {
        line = viewModel.getLineById(trip.lineId)
        stops[0] = viewModel.getStopById(trip.startId)
        stops[1] = viewModel.getStopById(trip.endId)
        if(!showedStopDialog) {
            showedStopDialog = true
            if(stops[0] == Stop() || stops[1] == Stop())
                showUpdateStopDataDialog()
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(text = "Trip no "+trip.id.toString())
            Text(text = "Line " + line.name)
            Text(text = "Type: "+trip.type.name)
            Text(text = "Start "+stops[0].name)
            Text(text = "End "+stops[1].name)
//            Text(text = "asidjbas"+uiState.stops[0])
        }
    }
}
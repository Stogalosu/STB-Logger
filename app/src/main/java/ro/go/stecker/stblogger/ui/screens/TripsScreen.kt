package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.data.database.entities.getFormattedDate
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState
import ro.go.stecker.stblogger.ui.vectors.ArrowLongDown

val discardRed = Color(224, 65, 65)
val confirmGreen = Color(87, 201, 90)

var deleteTripDialog by mutableStateOf(false)
var tripToDelete by mutableStateOf(Trip())
var tripToDeleteLine by mutableStateOf(Line())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onInfoClick: (Int) -> Unit,
    viewModel: StbViewModel,
    uiState: UiState
) {

    val coroutineScope = rememberCoroutineScope()

    if(deleteTripDialog) {
        AlertDialog(
            onDismissRequest = { deleteTripDialog = false },
            icon = { Icon(painter = painterResource(R.drawable.delete_24px), contentDescription = stringResource(R.string.delete_trip)) },
            title = { Text(stringResource(R.string.delete_trip)) },
            text = { Text(stringResource(R.string.delete_trip_dialog, tripToDelete.id.toString(), tripToDeleteLine.name, tripToDelete.getFormattedDate())) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteTripDialog = false
                        coroutineScope.launch { viewModel.deleteTrip(tripToDelete.id) }
                    }
                ) {
                    Text(text = stringResource(R.string.delete), color = discardRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deleteTripDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            StbTopAppBar(
                canNavigateBack = false,
                canSearch = true,
                uiState = uiState
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
                TripCard(
                    trip = trip,
                    onInfoClick = onInfoClick,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    onInfoClick: (Int) -> Unit,
    uiState: UiState,
    viewModel: StbViewModel
) {
    var line by remember { mutableStateOf(Line()) }

    var startStop by remember { mutableStateOf(Stop()) }
    var endStop by remember { mutableStateOf(Stop()) }

    LaunchedEffect(uiState.databaseUpdateStatus, Unit) {
        line = viewModel.getLineById(trip.lineId)
        startStop = viewModel.getStopById(trip.startId)
        endStop = viewModel.getStopById(trip.endId)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, line.type.color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
//            GlideImage(
//                contentDescription = null
//            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = startStop.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        Icon(
                            imageVector = ArrowLongDown,
                            contentDescription = null,
                            modifier = Modifier
                                .offset((-12).dp)
                                .size(58.dp)
                        )

                        Column(
                            modifier = Modifier.offset((-18).dp)
                        ) {
                            Text(
                                text = trip.getFormattedDate(),
                                fontSize = 14.sp,
                                modifier = Modifier.offset(y = (4).dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(line.type.icon),
                                    contentDescription = line.type.name,
                                    tint = line.type.color,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .offset((-8).dp)
                                )
                                Text(
                                    text = line.name,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.offset((-12).dp)
                                )
                            }
                        }
                    }


                    Text(
                        text = endStop.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.weight(1f))

                Column {
                    IconButton(
                        onClick = { onInfoClick(trip.id) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.info_24px),
                            contentDescription = stringResource(R.string.more_info),
                            tint = Color(207, 207, 207)
                        )
                    }
                    IconButton(
                        onClick = {
                            tripToDelete = trip
                            tripToDeleteLine = line
                            deleteTripDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete_24px),
                            contentDescription = stringResource(R.string.delete_trip),
                            tint = discardRed
                        )
                    }
                }
            }
        }
    }
}
package ro.go.stecker.stblogger.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.data.database.entities.getFormattedDate
import ro.go.stecker.stblogger.data.mapUrlGen
import ro.go.stecker.stblogger.getActivity
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
    onNewTripClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: StbViewModel,
    uiState: UiState
) {

    var fabHeight by remember { mutableStateOf(0.dp) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val failedSearchText = stringResource(R.string.no_stop_line_found)

    BackHandler {
        if(uiState.showFilteredTrips) viewModel.showFilteredTrips(false)
        else context.getActivity()?.finish()
    }

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if(!uiState.showFilteredTrips) {
            TripList(
                trips = uiState.trips,
                emptyListText = stringResource(R.string.no_trips),
                onInfoClick = onInfoClick,
                fabHeight = fabHeight,
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            TripList(
                trips = uiState.filteredTrips,
                emptyListText = stringResource(R.string.no_trips_found),
                onInfoClick = onInfoClick,
                fabHeight = fabHeight,
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun TripList(
    trips: List<Trip>,
    emptyListText: String,
    onInfoClick: (Int) -> Unit,
    fabHeight: Dp,
    uiState: UiState,
    viewModel: StbViewModel,
    modifier: Modifier = Modifier
) {
    if (trips.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            items(items = trips, key = { it.id }) { trip ->
                Box(modifier = Modifier.animateItem()) {
                    TripCard(
                        trip = trip,
                        onInfoClick = onInfoClick,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(fabHeight + 20.dp))
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = emptyListText,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
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
    var url by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.databaseUpdateStatus, Unit) {
        url = mapUrlGen(trip, 1f, viewModel)
        line = viewModel.getLineById(trip.lineId)
        startStop = viewModel.getStopById(trip.startId)
        endStop = viewModel.getStopById(trip.endId)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, line.type.color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(url != null)
                GlideImage(
                    model = url,
                    contentDescription = null
                )
            FlowRow(
                verticalArrangement = Arrangement.Center,
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
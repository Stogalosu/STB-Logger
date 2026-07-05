package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.data.getStopsFromTrip
import ro.go.stecker.stblogger.data.mapUrlGen
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TripInfoScreen(
    trip: Trip,
    onNavigateBack: () -> Unit,
    uiState: UiState,
    viewModel: StbViewModel
) {
    var line by remember { mutableStateOf("") }
    var intermediaryStops by remember { mutableStateOf(listOf<Stop>()) }
    var url by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        line = viewModel.getLineById(trip.lineId).name
        url = mapUrlGen(trip, 2f, viewModel)
        intermediaryStops = getStopsFromTrip(
            trip = trip,
            viewModel = viewModel
        ) ?: listOf()
    }

    Scaffold(
        topBar = {
            StbTopAppBar(
                title = stringResource(R.string.trip_with, line),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                canSearch = false,
                uiState = uiState
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {paddingValues ->
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                if (url != null)
                    item {
                        GlideImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                item {
                    Text(
                        text = stringResource(R.string.intermediary_stops),
                        fontWeight = FontWeight.Bold
                    )
                }
                items(intermediaryStops) { stop ->
                    Text(text = stop.name)
                }
            }
        }
    }
}
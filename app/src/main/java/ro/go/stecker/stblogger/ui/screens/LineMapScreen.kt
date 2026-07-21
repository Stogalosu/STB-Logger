package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState


@Composable
fun LineMapScreen(
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: StbViewModel
) {
    Scaffold(
        topBar = {
            StbTopAppBar(
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                canSearch = false,
                uiState = uiState,
                viewModel = viewModel
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            style = { MapStyle(style = "mapbox://styles/stogalosu/cmlm6u4q9000301qu8tulg3am") },
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(2.0)
                    center(Point.fromLngLat(-98.0, 39.5))
                    pitch(0.0)
                    bearing(0.0)
                }
            },
            compass = {
                Compass(
                    contentPadding = innerPadding,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            scaleBar = {
                ScaleBar(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(start = 8.dp)
                )
            },
            logo = {
                Logo(Modifier.padding(start = 8.dp, bottom = 8.dp))
            }
        )
    }
}
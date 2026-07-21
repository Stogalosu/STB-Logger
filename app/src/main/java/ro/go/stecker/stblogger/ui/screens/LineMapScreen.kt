package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState


@Composable
fun LineMapScreen(
    lineName: String,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: StbViewModel
) {
    Scaffold(
        topBar = {
            StbTopAppBar(
                title = stringResource(R.string.line_x, lineName),
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
        val style =
            @Composable {
                MapStyle(
                    style = "mapbox://styles/stogalosu/cmlm6u4q9000301qu8tulg3am",
                    styleImportsContent = {
                        MapEffect(Unit) { mapView ->
                            mapView.mapboxMap.getStyle { style ->
                                style.setStyleLayerProperty(
                                    layerId = lineName,
                                    property = "visibility",
                                    value = Value.valueOf("visible")
                                )
                            }
                        }
                    }
                )
        }

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            style = style,
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(10.0)
                    center(Point.fromLngLat(26.0996,44.4308))
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
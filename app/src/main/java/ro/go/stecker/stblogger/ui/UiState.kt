package ro.go.stecker.stblogger.ui

import ro.go.stecker.stblogger.data.Stop
import ro.go.stecker.stblogger.data.Trip

data class UiState(
    val trips: List<Trip> = emptyList(),
    val stops: List<Stop> = emptyList(),
    var cannotUpdateTripsDialog: Boolean = false
)
package ro.go.stecker.stblogger.ui

import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.ui.navigation.StbTab

enum class UpdateStatus {
    NotUpdated,
    Updating,
    Updated
}

data class UiState(
    val tab: StbTab = StbTab.Trips,
    val trips: List<Trip> = emptyList(),
    val filteredTrips: List<Trip> = emptyList(),
    val filteredLines: List<Line> = emptyList(),
    val isConnected: Boolean = true,
    val databaseUpdateStatus: UpdateStatus = UpdateStatus.NotUpdated,
    val cannotUpdateTripsDialog: Boolean = false,
    val noInternetDialog: Boolean = false,
    val showFilteredTrips: Boolean = false,
    val showFilteredLines: Boolean = false
)
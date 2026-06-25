package ro.go.stecker.stblogger.ui

import ro.go.stecker.stblogger.data.database.entities.Trip

enum class UpdateStatus {
    NotUpdated,
    Updating,
    Updated
}

data class UiState(
    val trips: List<Trip> = emptyList(),
    val isConnected: Boolean = true,
    val databaseUpdateStatus: UpdateStatus = UpdateStatus.NotUpdated,
    var cannotUpdateTripsDialog: Boolean = false,
    var noInternetDialog: Boolean = false
)
package ro.go.stecker.stblogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.data.CloudRepository
import ro.go.stecker.stblogger.data.Trip
import ro.go.stecker.stblogger.data.TripType
import ro.go.stecker.stblogger.data.database.DatabaseRepository

class StbViewModel(databaseRepository: DatabaseRepository, cloudRepository: CloudRepository): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val trips = listOf(
            Trip(
                id = 1,
                type = TripType.Tram,
                startId = 70,
                endId = 90
            )
        )
        _uiState.update { it.copy(trips = trips) }

        cloudRepository.getStopDatabase(
            onSuccess = {
                _uiState.update { state -> state.copy(stops = it) }
                viewModelScope.launch { databaseRepository.insertStopList(it) }
            }
        )
    }

}
package ro.go.stecker.stblogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.data.CloudRepository
import ro.go.stecker.stblogger.data.Line
import ro.go.stecker.stblogger.data.Stop
import ro.go.stecker.stblogger.data.Trip
import ro.go.stecker.stblogger.data.TripType
import ro.go.stecker.stblogger.data.database.DatabaseRepository
import ro.go.stecker.stblogger.network.NetworkConnectivityObserver

class StbViewModel(
    private val databaseRepository: DatabaseRepository,
    private val cloudRepository: CloudRepository,
    private val connectivityObserver: NetworkConnectivityObserver
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
//            combine(
//                 connectivityObserver.observe()
//            ) { networkStatus ->
//                UiState(
//                    networkStatus = networkStatus
//                )
//            }.collect { value -> _uiState.value = value }

            val trips = listOf(
                Trip(
                    id = 1,
                    type = TripType.Tram,
                    lineId = "146",
                    startId = 6021,
                    endId = 7046
                )
            )

            connectivityObserver.isConnected.collect { value ->
                _uiState.value = UiState(trips = trips, isConnected = value)
            }

        }
    }

    suspend fun getLineById(id: String, rep: Int = 1) : Line {
        val result = databaseRepository.getLineById(id)
        if(result == null) {
            updateLineDatabase()
            delay(1000)
            if(rep>=3) return Line()
            else return getLineById(id, rep+1)
        }
        else return result
    }

    fun updateLineDatabase() {
        if(_uiState.value.isConnected)
            cloudRepository.getLineDatabase { list ->
                viewModelScope.launch {
                    databaseRepository.insertLineList(
                        lineList = list
                    )
                    _uiState.update { it.copy(updatedDatabase = it.updatedDatabase+1) }
                }
            }
    }

    suspend fun getStopById(id: Int): Stop {
        return databaseRepository.getStopById(id) ?: Stop()
    }

    fun updateStopDatabase(onFinish: () -> Unit) {
        if(_uiState.value.isConnected)
            cloudRepository.getStopDatabase { list ->
                viewModelScope.launch {
                    databaseRepository.insertStopList(
                        stopList = list
                    )
                    onFinish()
                    _uiState.update { it.copy(updatedDatabase = it.updatedDatabase+1) }
                }
            }
    }

}
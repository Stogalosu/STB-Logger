package ro.go.stecker.stblogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.data.firebase.firestore.CloudRepository
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.data.database.DatabaseRepository
import ro.go.stecker.stblogger.data.database.entities.Path
import ro.go.stecker.stblogger.data.firebase.functions.FunctionsRepository
import ro.go.stecker.stblogger.data.database.entities.lineType
import ro.go.stecker.stblogger.data.datastore.DataStoreRepository
import ro.go.stecker.stblogger.network.NetworkConnectivityObserver
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StbViewModel(
    private val databaseRepository: DatabaseRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val functionsRepository: FunctionsRepository,
    private val cloudRepository: CloudRepository,
    private val connectivityObserver: NetworkConnectivityObserver
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> =
        combine(
            databaseRepository.getTrips(),
            connectivityObserver.isConnected,
            _uiState
        ) { tripList, isConnected, _uiState ->
            _uiState.copy(
                trips = tripList,
                isConnected = isConnected
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState()
        )

    lateinit var updates: List<Boolean>

    init {
        viewModelScope.launch {
            updates = listOf(
                functionsRepository.shouldUpdateDatabase(
                    "paths",
                    dataStoreRepository.getUpdateTimestamp("paths")
                ),
                functionsRepository.shouldUpdateDatabase(
                    "lines",
                    dataStoreRepository.getUpdateTimestamp("lines")
                ),
                functionsRepository.shouldUpdateDatabase(
                    "stops",
                    dataStoreRepository.getUpdateTimestamp("stops")
                )
            )
            if(updates.contains(true))
                _uiState.update { it.copy(databaseUpdateStatus = UpdateStatus.Updating) }

//            val trip =
//                Trip(
//                    type = TripType.Bus,
//                    lineId = "887",
//                    startId = 6626,
//                    endId = 6044,
//                    date = "2026-02-04"
//                )
//
//            databaseRepository.insertTrip(trip)
        }
    }

    suspend fun markDatabasesAsUpdated() {
        dataStoreRepository.setUpdateTimestamp("paths")
        dataStoreRepository.setUpdateTimestamp("lines")
        dataStoreRepository.setUpdateTimestamp("stops")
        _uiState.update { it.copy(databaseUpdateStatus = UpdateStatus.Updated) }
    }

    suspend fun getLineById(id: String) : Line {
        return databaseRepository.getLineById(id) ?: Line()
    }

    suspend fun getLines(): List<Line> = databaseRepository.getLines()

    suspend fun updateLineDatabase() {
        if(_uiState.value.isConnected) {
            val shouldUpdate = updates[1]
            if(shouldUpdate) {
                val list = suspendCoroutine { continuation ->
                    cloudRepository.getLineDatabase { lines ->
                        continuation.resume(lines)
                    }
                }
                list.forEach { it.type = lineType(it.name) }
                databaseRepository.insertLineList(lineList = list)
            }
        }
    }

    suspend fun getStopById(id: Int): Stop {
        return databaseRepository.getStopById(id) ?: Stop()
    }

    suspend fun getStops(): List<Stop> = databaseRepository.getStops()

    suspend fun getStopsOnLine(line: String): List<Pair<Stop, Int>> = databaseRepository.getStopsOnLine(line)
    suspend fun searchStops(query: String): List<String> = databaseRepository.searchStops(query)

    suspend fun updateStopDatabase() {
        if(_uiState.value.isConnected) {
            val shouldUpdate = updates[2]
            if (shouldUpdate) {
                val list = suspendCoroutine { continuation ->
                    cloudRepository.getStopDatabase { stops ->
                        continuation.resume(stops)
                    }
                }
                databaseRepository.insertStopList(stopList = list)
            }
        }
    }

    suspend fun getPaths(): List<Path> = databaseRepository.getPaths()

    suspend fun updatePathDatabase() {
        if(_uiState.value.isConnected) {
            val shouldUpdate = updates[0]
            if (shouldUpdate) {
                val list = suspendCoroutine { continuation ->
                    cloudRepository.getPathDatabase { paths ->
                        continuation.resume(paths)
                    }
                }
                databaseRepository.insertPathList(pathList = list)
            }
        }
    }

    suspend fun insertTrip(trip: Trip) = databaseRepository.insertTrip(trip)

    suspend fun searchTrips(
        startStopName: String = "%",
        endStopName: String = "%",
        lineName: String = "%",
        date: String = "%"
    ) = databaseRepository.searchTrips(startStopName, endStopName, lineName, date)

    fun showFilteredTrips(active: Boolean, filteredTrips: List<Trip> = listOf()) {
        if(filteredTrips.isNotEmpty())
            _uiState.update { it.copy(showFilteredTrips = active, filteredTrips = filteredTrips) }
        else
            _uiState.update { it.copy(showFilteredTrips = active) }
    }

    suspend fun deleteTrip(id: Int) = databaseRepository.deleteTrip(id)

}
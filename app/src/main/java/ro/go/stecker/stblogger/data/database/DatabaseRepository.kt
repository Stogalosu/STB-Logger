package ro.go.stecker.stblogger.data.database

import kotlinx.coroutines.flow.Flow
import ro.go.stecker.stblogger.data.Line
import ro.go.stecker.stblogger.data.Stop
import ro.go.stecker.stblogger.data.Trip

interface DatabaseRepo {
    suspend fun insertStopList(stopList: List<Stop>)
    suspend fun insertLineList(lineList: List<Line>)
    suspend fun getLineById(id: String): Line?
    suspend fun getStopById(id: Int): Stop?
    fun getTrips(): Flow<List<Trip>>
    suspend fun insertTrip(trip: Trip)
    suspend fun deleteTrip(id: Int)
}

class DatabaseRepository(private val stbDao: StbDao): DatabaseRepo {
    override suspend fun insertStopList(stopList: List<Stop>) = stbDao.insertStopList(stopList)
    override suspend fun insertLineList(lineList: List<Line>) = stbDao.insertLineList(lineList)
    override suspend fun getLineById(id: String): Line? = stbDao.getLineById(id)
    override suspend fun getStopById(id: Int): Stop? = stbDao.getStopById(id)
    override fun getTrips(): Flow<List<Trip>> = stbDao.getTrips()
    override suspend fun insertTrip(trip: Trip) = stbDao.insertTrip(trip)
    override suspend fun deleteTrip(id: Int) = stbDao.deleteTrip(id)
}
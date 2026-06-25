package ro.go.stecker.stblogger.data.database

import kotlinx.coroutines.flow.Flow
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Path
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip

interface DatabaseRepo {
    suspend fun insertStopList(stopList: List<Stop>)
    suspend fun insertLineList(lineList: List<Line>)
    suspend fun insertPathList(pathList: List<Path>)
    suspend fun getLineById(id: String): Line?
    suspend fun getLines(): List<Line>
    suspend fun getStops(): List<Stop>
    suspend fun getStopById(id: Int): Stop?
    fun getTrips(): Flow<List<Trip>>
    suspend fun insertTrip(trip: Trip)
    suspend fun deleteTrip(id: Int)
    suspend fun getPaths(): List<Path>
}

class DatabaseRepository(private val stbDao: StbDao): DatabaseRepo {
    override suspend fun insertStopList(stopList: List<Stop>) = stbDao.insertStopList(stopList)
    override suspend fun insertLineList(lineList: List<Line>) = stbDao.insertLineList(lineList)
    override suspend fun insertPathList(pathList: List<Path>) = stbDao.insertPathList(pathList)
    override suspend fun getLineById(id: String): Line? = stbDao.getLineById(id)
    override suspend fun getLines(): List<Line> = stbDao.getLines()
    override suspend fun getStops(): List<Stop> = stbDao.getStops()
    override suspend fun getStopById(id: Int): Stop? = stbDao.getStopById(id)
    override fun getTrips(): Flow<List<Trip>> = stbDao.getTrips()
    override suspend fun insertTrip(trip: Trip) = stbDao.insertTrip(trip)
    override suspend fun deleteTrip(id: Int) = stbDao.deleteTrip(id)
    override suspend fun getPaths(): List<Path> = stbDao.getPaths()
}
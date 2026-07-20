package ro.go.stecker.stblogger.data.database

import android.util.Log
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
    suspend fun getLineByName(name: String): Line?
    suspend fun getLines(): List<Line>
    suspend fun searchLines(query: String): List<Line>
    suspend fun getStops(): List<Stop>
    suspend fun getStopById(id: Int): Stop?
    suspend fun getStopsByName(name: String): List<Stop>
    suspend fun searchStops(query: String): List<Stop>
    suspend fun getPathsOnLine(line: String): List<Path>
    suspend fun getStopsOnLine(line: String): List<Pair<Stop, Int>>
    fun getTrips(): Flow<List<Trip>>
    suspend fun insertTrip(trip: Trip)
    suspend fun searchTrips(
        startStopNames: List<String>,
        endStopNames: List<String>,
        lineNames: List<String>,
        dates: List<String>
    ): List<Trip>
    suspend fun deleteTrip(id: Int)
    suspend fun getPaths(): List<Path>
}

class DatabaseRepository(private val stbDao: StbDao): DatabaseRepo {
    override suspend fun insertStopList(stopList: List<Stop>) = stbDao.insertStopList(stopList)
    override suspend fun insertLineList(lineList: List<Line>) = stbDao.insertLineList(lineList)
    override suspend fun insertPathList(pathList: List<Path>) = stbDao.insertPathList(pathList)
    override suspend fun getLineById(id: String): Line? = stbDao.getLineById(id)
    override suspend fun getLineByName(name: String): Line? = stbDao.getLineByName(name)
    override suspend fun getLines(): List<Line> {
        val lines = stbDao.getLines()
        return lines.sortedWith(compareBy ({ it.type.ordinal }, { it.name }))
    }
    override suspend fun searchLines(query: String): List<Line> = stbDao.searchLines("%$query%")
    override suspend fun getStops(): List<Stop> = stbDao.getStops()
    override suspend fun getStopById(id: Int): Stop? = stbDao.getStopById(id)
    override suspend fun getStopsByName(name: String): List<Stop> = stbDao.getStopsByName(name)
    override suspend fun searchStops(query: String): List<Stop> {
        var newQuery = query
        newQuery.replace('ă', 'a')
        newQuery.replace('â', 'a')
        newQuery.replace('î', 'i')
        newQuery.replace('ș', 's')
        newQuery.replace('ț', 't')
        val words = newQuery.split(' ')
        words.forEach { word ->
            if(word.isNotEmpty()) {
                if (word[0].isLowerCase())
                    word.replaceFirst(word[0].toString(), "%[${word[0]}${word[0].uppercaseChar()}")
                else
                    word.replaceFirst(word[0].toString(), "%[${word[0]}${word[0].lowercaseChar()}")
            }
        }
        newQuery = words.joinToString(" ") + "%"
        return stbDao.searchStops(newQuery)
    }
    override suspend fun getPathsOnLine(line: String): List<Path> = stbDao.getPathsOnLine(line)
    override suspend fun getStopsOnLine(line: String): List<Pair<Stop, Int>> {
        val paths = getPathsOnLine(line)
        val stops = mutableListOf<Pair<Stop, Int>>()
        for(i in 0..paths.size-1) {
            val stop = getStopById(paths[i].startId)
            if (stop != null) {
                val item = Pair(stop, paths[i].direction)
                if (!stops.contains(item))
                    stops.add(item)
            } else
                Log.e("stop", "Failed to find stop with id ${paths[i].startId}")
        }
        return stops
    }
    override fun getTrips(): Flow<List<Trip>> = stbDao.getTrips()
    override suspend fun insertTrip(trip: Trip) = stbDao.insertTrip(trip)
    override suspend fun searchTrips(
        startStopNames: List<String>,
        endStopNames: List<String>,
        lineNames: List<String>,
        dates: List<String>
    ): List<Trip> {

        val startIds = mutableListOf<Int>()
        startStopNames.forEach { startStopName ->
            startIds.addAll(getStopsByName(startStopName).map { it.id })
        }
        val endIds = mutableListOf<Int>()
        endStopNames.forEach { endStopName ->
            endIds.addAll(getStopsByName(endStopName).map { it.id })
        }
        val lineIds = mutableListOf<String>()
        lineNames.forEach { lineName ->
            lineIds.add(getLineByName(lineName)!!.id)
        }

        return stbDao.searchTrips(
            startIds,
            startIds.isNotEmpty(),
            endIds,
            endIds.isNotEmpty(),
            lineIds,
            lineIds.isNotEmpty(),
            dates,
            dates.isNotEmpty()
        )
    }
    override suspend fun deleteTrip(id: Int) = stbDao.deleteTrip(id)
    override suspend fun getPaths(): List<Path> = stbDao.getPaths()
}
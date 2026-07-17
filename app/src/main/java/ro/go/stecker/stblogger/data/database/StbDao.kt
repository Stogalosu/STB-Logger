package ro.go.stecker.stblogger.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Path
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip

@Dao
interface StbDao {
    @Insert(entity = Stop::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStopList(stopList: List<Stop>)

    @Insert(entity = Line::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineList(lineList: List<Line>)

    @Insert(entity = Path::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPathList(pathList: List<Path>)

    @Query("SELECT * FROM line WHERE id = :id LIMIT 1")
    suspend fun getLineById(id: String): Line?

    @Query("SELECT * FROM line WHERE name = :name LIMIT 1")
    suspend fun getLineByName(name: String): Line?

    @Query("SELECT * FROM line")
    suspend fun getLines(): List<Line>

    @Query("SELECT * FROM line WHERE name LIKE :query")
    suspend fun searchLines(query: String): List<Line>

    @Query("SELECT * FROM stop")
    suspend fun getStops(): List<Stop>

    @Query("SELECT * FROM stop WHERE id = :id LIMIT 1")
    suspend fun getStopById(id: Int): Stop?

    @Query("SELECT * FROM stop WHERE name = :name")
    suspend fun getStopsByName(name: String): List<Stop>

    @Query("SELECT * FROM stop WHERE name LIKE :query")
    suspend fun searchStops(query: String): List<Stop>

    @Query("SELECT * FROM path WHERE line = :line")
    suspend fun getPathsOnLine(line: String): List<Path>

    @Query("SELECT * FROM trip")
    fun getTrips(): Flow<List<Trip>>

    @Insert(entity = Trip::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Query("""
        SELECT * FROM trip 
        WHERE (:useStartIds = false OR startId IN (:startIds))
        AND (:useEndIds = false OR endId IN (:endIds))
        AND (:useLineIds = false OR lineId IN (:lineIds))
        AND (:useDates = false OR date IN (:dates))
    """)
    suspend fun searchTrips(
        startIds: List<Int>,
        useStartIds: Boolean,
        endIds: List<Int>,
        useEndIds: Boolean,
        lineIds: List<String>,
        useLineIds: Boolean,
        dates: List<String>,
        useDates: Boolean
    ): List<Trip>

    @Query("DELETE FROM trip WHERE id = :id")
    suspend fun deleteTrip(id: Int)

    @Query("SELECT * FROM path")
    suspend fun getPaths(): List<Path>

}
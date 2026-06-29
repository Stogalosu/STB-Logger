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

    @Query("SELECT * FROM line")
    suspend fun getLines(): List<Line>

    @Query("SELECT * FROM stop")
    suspend fun getStops(): List<Stop>

    @Query("SELECT * FROM stop WHERE id = :id LIMIT 1")
    suspend fun getStopById(id: Int): Stop?

    @Query("SELECT * FROM path WHERE line = :line")
    suspend fun getPathsOnLine(line: String): List<Path>

    @Query("SELECT * FROM trip")
    fun getTrips(): Flow<List<Trip>>

    @Insert(entity = Trip::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Query("DELETE FROM trip WHERE id = :id")
    suspend fun deleteTrip(id: Int)

    @Query("SELECT * FROM path")
    suspend fun getPaths(): List<Path>

}
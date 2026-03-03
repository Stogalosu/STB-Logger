package ro.go.stecker.stblogger.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ro.go.stecker.stblogger.data.Line
import ro.go.stecker.stblogger.data.Stop

@Dao
interface StbDao {
    @Insert(entity = Stop::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStopList(stopList: List<Stop>)

    @Insert(entity = Line::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineList(lineList: List<Line>)

    @Query("SELECT * FROM line WHERE id = :id LIMIT 1")
    suspend fun getLineById(id: String): Line?

    @Query("SELECT * FROM stop WHERE id = :id LIMIT 1")
    suspend fun getStopById(id: Int): Stop?


}
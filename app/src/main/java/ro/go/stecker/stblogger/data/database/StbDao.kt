package ro.go.stecker.stblogger.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ro.go.stecker.stblogger.data.Stop

@Dao
interface StbDao {
    @Insert(entity = Stop::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStopList(stopsList: List<Stop>)


}
package ro.go.stecker.stblogger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Path
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip

@Database(entities = [Stop::class, Line::class, Trip::class, Path::class], version = 1, exportSchema = false)
abstract class StbLoggerDatabase: RoomDatabase() {
    abstract fun stbDao(): StbDao

    companion object {
        private var Instance: StbLoggerDatabase? = null
        fun getDatabase(context: Context): StbLoggerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, StbLoggerDatabase::class.java, "stb_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
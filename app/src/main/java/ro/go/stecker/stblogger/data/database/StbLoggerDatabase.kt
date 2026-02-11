package ro.go.stecker.stblogger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ro.go.stecker.stblogger.data.Line
import ro.go.stecker.stblogger.data.Stop
import ro.go.stecker.stblogger.data.Trip

@Database(entities = [Stop::class, Line::class, Trip::class], version = 1, exportSchema = false)
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
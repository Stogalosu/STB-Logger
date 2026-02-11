package ro.go.stecker.stblogger.data

import android.content.Context
import ro.go.stecker.stblogger.data.database.DatabaseRepository
import ro.go.stecker.stblogger.data.database.StbLoggerDatabase

interface AppContainer {
    val cloudRepository: CloudRepository
    val databaseRepository: DatabaseRepository
}

class AppDataContainer(val context: Context): AppContainer {
    override val cloudRepository = CloudRepository()
    override val databaseRepository = DatabaseRepository(StbLoggerDatabase.getDatabase(context).stbDao())
}
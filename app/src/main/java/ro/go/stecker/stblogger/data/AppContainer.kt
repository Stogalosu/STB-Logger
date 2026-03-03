package ro.go.stecker.stblogger.data

import android.content.Context
import ro.go.stecker.stblogger.data.database.DatabaseRepository
import ro.go.stecker.stblogger.data.database.StbLoggerDatabase
import ro.go.stecker.stblogger.network.NetworkConnectivityObserver

interface AppContainer {
    val cloudRepository: CloudRepository
    val databaseRepository: DatabaseRepository
    val connectivityObserver: NetworkConnectivityObserver
}

class AppDataContainer(val context: Context): AppContainer {
    override val cloudRepository = CloudRepository()
    override val databaseRepository = DatabaseRepository(StbLoggerDatabase.getDatabase(context).stbDao())
    override val connectivityObserver = NetworkConnectivityObserver(context)
}
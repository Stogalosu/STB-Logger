package ro.go.stecker.stblogger.data

import android.content.Context
import ro.go.stecker.stblogger.data.database.DatabaseRepository
import ro.go.stecker.stblogger.data.database.StbLoggerDatabase
import ro.go.stecker.stblogger.data.datastore.DataStoreRepository
import ro.go.stecker.stblogger.data.firebase.firestore.CloudRepository
import ro.go.stecker.stblogger.data.firebase.functions.FunctionsRepository
import ro.go.stecker.stblogger.network.NetworkConnectivityObserver

interface AppContainer {
    val cloudRepository: CloudRepository
    val functionsRepository: FunctionsRepository
    val databaseRepository: DatabaseRepository
    val dataStoreRepository: DataStoreRepository
    val connectivityObserver: NetworkConnectivityObserver
}

class AppDataContainer(val context: Context): AppContainer {
    override val cloudRepository = CloudRepository()
    override val functionsRepository = FunctionsRepository()
    override val databaseRepository = DatabaseRepository(StbLoggerDatabase.getDatabase(context).stbDao())
    override val dataStoreRepository = DataStoreRepository(context)
    override val connectivityObserver = NetworkConnectivityObserver(context)
}
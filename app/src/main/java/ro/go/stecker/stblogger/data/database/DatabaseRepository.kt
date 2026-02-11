package ro.go.stecker.stblogger.data.database

import ro.go.stecker.stblogger.data.Stop

interface DatabaseRepo {
    suspend fun insertStopList(stopsList: List<Stop>)
}

class DatabaseRepository(private val stbDao: StbDao): DatabaseRepo {
    override suspend fun insertStopList(stopsList: List<Stop>) = stbDao.insertStopList(stopsList)
}
package ro.go.stecker.stblogger.data.database

import ro.go.stecker.stblogger.data.Line
import ro.go.stecker.stblogger.data.Stop

interface DatabaseRepo {
    suspend fun insertStopList(stopList: List<Stop>)
    suspend fun insertLineList(lineList: List<Line>)
    suspend fun getLineById(id: String): Line?
    suspend fun getStopById(id: Int): Stop?
}

class DatabaseRepository(private val stbDao: StbDao): DatabaseRepo {
    override suspend fun insertStopList(stopList: List<Stop>) = stbDao.insertStopList(stopList)
    override suspend fun insertLineList(lineList: List<Line>) = stbDao.insertLineList(lineList)
    override suspend fun getLineById(id: String): Line? = stbDao.getLineById(id)
    override suspend fun getStopById(id: Int): Stop? = stbDao.getStopById(id)
}
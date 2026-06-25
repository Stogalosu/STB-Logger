package ro.go.stecker.stblogger.data.firebase.functions

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

interface FunctionsRepo {
    suspend fun shouldUpdateDatabase(collection: String, lastUpdated: Long): Boolean
}

class FunctionsRepository(): FunctionsRepo {
    val functions = FirebaseFunctions.getInstance("europe-central2")

    override suspend fun shouldUpdateDatabase(collection: String, lastUpdated: Long): Boolean {
        val json = JSONObject()
        json.accumulate("collection", collection)
        json.accumulate("lastUpdated", lastUpdated)
        var result: Boolean? = true
        try {
            val call = functions
                .getHttpsCallable("checkDatabaseUpdates")
                .call(json)
                .await()
//            Log.e("test", call.toString())
            result = call.data as? Boolean
        } catch (e: Exception) {
            result = true
//            Log.e("test error", e.toString())
        }
//        Log.w("test", result.toString())
        return result ?: true
    }
}
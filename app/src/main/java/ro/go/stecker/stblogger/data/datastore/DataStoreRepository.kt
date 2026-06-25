package ro.go.stecker.stblogger.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlin.also


interface DataStoreRepo {
    suspend fun setUpdateTimestamp(list: String)
    suspend fun getUpdateTimestamp(list: String): Long
}

class DataStoreRepository(val context: Context): DataStoreRepo {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("STBDataStore")

    override suspend fun setUpdateTimestamp(list: String) {
        val key = longPreferencesKey(list)
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[key] = System.currentTimeMillis()
            }
        }
    }

    override suspend fun getUpdateTimestamp(list: String): Long {
        val key = longPreferencesKey(list)
        val preferences = context.dataStore.data.first()
        return preferences[key] ?: 0
    }
}
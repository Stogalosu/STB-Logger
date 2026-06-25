package ro.go.stecker.stblogger.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Path(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val line: String = "",
    val direction: Int = 0,
    val order: Int = 0,
    val startId: Int = 0
)
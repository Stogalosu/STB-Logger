package ro.go.stecker.stblogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TripType {
    Undefined,
    Tram,
    Trolleybus,
    Bus,
    Subway,
    Mixed
}

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val type: TripType,
    val startId: Int,
    val endId: Int,
)

package ro.go.stecker.stblogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TripType {
    Undefined,
    Tram,
    Trolleybus,
    Bus,
    Subway
}

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val type: TripType = TripType.Undefined,
    val lineId: String = "",
    val startId: Int = 0,
    val endId: Int = 0,
)

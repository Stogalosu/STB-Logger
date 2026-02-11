package ro.go.stecker.stblogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class StopType {
    Tram,
    Bus,
    Subway
}

@Entity
data class Stop(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val name: String = "",
    val type: StopType = StopType.Tram,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

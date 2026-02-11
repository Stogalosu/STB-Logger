package ro.go.stecker.stblogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LineType {
    Tram,
    Trolleybus,
    Bus,
    Subway
}

@Entity
data class Line(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: Int,
    val bis: Boolean = false,
    val type: LineType
)

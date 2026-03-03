package ro.go.stecker.stblogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LineType {
    Undefined,
    Tram,
    Trolleybus,
    Bus,
    Subway
}

@Entity
data class Line(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val name: String = "",
    val type: LineType = lineType(name)
)

fun lineType(name: String): LineType {
    val nameToInt = name.toIntOrNull()
    return if(nameToInt != null) {
        if (nameToInt < 60) LineType.Tram
        else if (nameToInt < 100) LineType.Trolleybus
        else LineType.Bus
    } else {
        if(name.endsWith('B') || name.startsWith('N')) LineType.Bus
        else if(name.startsWith('M')) LineType.Subway
        else LineType.Undefined
    }
}

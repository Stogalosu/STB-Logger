package ro.go.stecker.stblogger.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import ro.go.stecker.stblogger.R

enum class LineType(
    @param:DrawableRes
    val icon: Int = R.drawable.block_24px,
    val color: Color = Color(0, 0, 0)
) {
    Undefined,
    Tram(icon = R.drawable.tram, color = Color(218, 11, 53)),
    Trolleybus(icon = R.drawable.trolleybus, color = Color(87, 201, 90)),
    Bus(icon = R.drawable.bus, color = Color(10, 92, 255)),
    Subway
}

@Entity
data class Line(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val name: String = "",
    var type: LineType = lineType(name)
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

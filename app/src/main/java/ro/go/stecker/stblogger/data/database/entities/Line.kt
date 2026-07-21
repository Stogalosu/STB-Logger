package ro.go.stecker.stblogger.data.database.entities

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.StbViewModel

enum class LineType(
    @param:DrawableRes
    val icon: Int = R.drawable.block_24px,
    val color: Color = Color(0, 0, 0)
) {
    Undefined,
    Tram(icon = R.drawable.tram, color = Color(/*218, 11, 53*/ 190, 22, 34)),
    Trolleybus(icon = R.drawable.trolleybus, color = Color(/*87, 201, 90*/ 0, 141, 54)),
    Bus(icon = R.drawable.bus, color = Color(/*10, 92, 255*/ 29, 113, 184)),
    NightBus(icon = R.drawable.bus, color = Color(45, 46, 131)),
    Subway(icon = R.drawable.subway_24px)
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
        if(name.endsWith('B')) LineType.Bus
        else if(name.startsWith('N')) LineType.NightBus
        else if(name.startsWith('M')) LineType.Subway
        else LineType.Undefined
    }
}

suspend fun Line.getTerminusStops(viewModel: StbViewModel): Pair<Stop, Stop> {
    val paths = viewModel.getPaths()

    val pathDir0 = paths.find {
        it.line == this.name &&
        it.direction == 0
    } ?: Path()
    val pathDir1 = paths.find {
        it.line == this.name &&
        it.direction == 1
    } ?: Path()
    val stopDir0 = viewModel.getStopById(pathDir0.startId)
    val stopDir1 = viewModel.getStopById(pathDir1.startId)

    return Pair(stopDir0, stopDir1)
}

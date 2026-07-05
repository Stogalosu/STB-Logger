package ro.go.stecker.stblogger.data

import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.BuildConfig

suspend fun mapUrlGen(
    trip: Trip,
    sizeMultiplier: Float,
    viewModel: StbViewModel
): String {
    val line = viewModel.getLineById(trip.lineId)
    val ACCESS_TOKEN = BuildConfig.MAPBOX_TOKEN
    val STYLE_ID = "cmlm6u4q9000301qu8tulg3am"
    val width = (200 * sizeMultiplier).toInt()
    val height = (200 * sizeMultiplier).toInt()

    val red = line.type.color.toArgb().red
    val green = line.type.color.toArgb().green
    val blue = line.type.color.toArgb().blue

    val stops = (getStopsFromTrip(trip, viewModel) ?: listOf()).toMutableList()
    stops.add(0, viewModel.getStopById(trip.startId))
    val idFilter =
        stops.joinToString(separator = ",") { stop ->
            "[\"==\",${stop.id},[\"get\",\"startId\"]]"
        }
    val layerString = """
{
"id":"${line.name}gggfgfg",
"type":"line",
"paint":{
"line-color":"rgb($red,$green,$blue)",
"line-width":2
},
"source":"composite",
"source-layer":"STB",
"filter":[
"all",
[
"any",
[
"in",
"[${line.name}]",
["get","path_lines"]
],
[
"in",
"[${line.name},",
["get","path_lines"]
],
[
"in",
",${line.name}]",
["get","path_lines"]
],
[
"in",
",${line.name},",
["get","path_lines"]
]
],
[
"any",
$idFilter
]
]
}
""".trimIndent().replace("\n", "")
    val s = "https://api.mapbox.com/styles/v1/stogalosu/$STYLE_ID/static/26.0996,44.4308,10,0,0/${width}x${height}@2x?attribution=false&logo=false&access_token=${ACCESS_TOKEN}&addlayer=${layerString}"
    return s
}

suspend fun getStopsFromTrip(
    trip: Trip,
    viewModel: StbViewModel
): List<Stop>? {
    val paths = viewModel.getPaths()
    val stops = viewModel.getStops()
    val list = mutableListOf<Stop>()

    val line = viewModel.getLineById(trip.lineId).name
    val firstStop = viewModel.getStopById(trip.startId)
    val lastStop = viewModel.getStopById(trip.endId)

    val firstPath = paths.find {
        it.startId == firstStop.id.toInt() &&
        it.line == line &&
        it.direction == trip.startDir
    }
    val lastPath = paths.find {
        it.startId == lastStop.id.toInt() &&
        it.line == line &&
        it.direction == trip.endDir
    }

    if(firstPath == null || lastPath == null) return null

    val firstIndex = paths.indexOf(firstPath)
    val lastIndex = paths.indexOf(lastPath)

    var currentDirection = trip.startDir
    var currentIndex = firstIndex + 1
    while(currentIndex != firstIndex && currentIndex != lastIndex) {
        if(paths[currentIndex].line == line && paths[currentIndex].direction == currentDirection) {
            list.add(stops.find { it.id.toInt() == paths[currentIndex].startId }!!)
            currentIndex++
        } else {
            currentDirection = trip.endDir
            currentIndex = paths.indexOf(paths.find { it.line == line && it.direction == currentDirection })
        }

    }
    return list
}
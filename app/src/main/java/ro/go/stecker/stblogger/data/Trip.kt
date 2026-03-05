package ro.go.stecker.stblogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val vehicle: Int = 0,
    val startId: Int = 0,
    val endId: Int = 0,
    val date: String = "1970-01-01"
)

fun Trip.getFormattedDate(): String  {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.parse(this.date).format(formatter)
}

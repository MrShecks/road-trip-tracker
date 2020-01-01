package ie.justonetech.roadtriptracker.utils

import android.content.Context
import ie.justonetech.roadtriptracker.model.TrackingStats

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingStatsFormatter
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingStatsFormatter(context: Context, data: TrackingStats, private val speedUnit: SpeedUnit, private val distanceUnit: DistanceUnit)
    : ModelFormatter<TrackingStats>(context, data) {

    val totalDuration: String
        get() = formatDuration(data.totalDuration, showZeroHours = true)

    val activeDuration: String
        get() = formatDuration(data.activeDuration, showZeroHours = true)

    val pausedDuration: String
        get() = formatDuration(data.totalDuration - data.activeDuration, showZeroHours = true)

    val distance: String
        get() = formatDistance(data.distance, distanceUnit)

    val currentSpeed: String
        get() = formatSpeed(data.currentSpeed, speedUnit)

    val maxSpeed: String
        get() = formatSpeed(data.maxSpeed, speedUnit)

    val avgSpeed: String
        get() = formatSpeed(data.avgSpeed, speedUnit)
}
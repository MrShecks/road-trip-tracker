package ie.justonetech.roadtriptracker.service

import java.util.*

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// LiveStats
/////////////////////////////////////////////////////////////////////////////////////////////////////////

data class LiveStats(
    val startTimestamp: Date,
    val totalDuration: Long,
    val activeDuration: Long,

    val distance: Double,

    val maxSpeed: Float,
    val avgSpeed: Float,
    val avgActiveSpeed: Float,
    val currentSpeed: Float,

    val maxElevationGain: Double,
    val totalElevationGain: Double
)
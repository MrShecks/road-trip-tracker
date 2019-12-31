package ie.justonetech.roadtriptracker.model

import java.util.*

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingStats
/////////////////////////////////////////////////////////////////////////////////////////////////////////

data class TrackingStats(
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
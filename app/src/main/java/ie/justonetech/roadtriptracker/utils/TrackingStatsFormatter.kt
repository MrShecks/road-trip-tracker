package ie.justonetech.roadtriptracker.utils

import android.content.Context
import ie.justonetech.roadtriptracker.model.TrackingStats

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingStatsFormatter
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingStatsFormatter(context: Context, data: TrackingStats)
    : ModelFormatter<TrackingStats>(context, data) {

    val totalDuration: String
        get() = formatDuration(data.totalDuration, showZeroHours = true)

    val activeDuration: String
        get() = formatDuration(data.activeDuration, showZeroHours = true)

    val pausedDuration: String
        get() = formatDuration(data.totalDuration - data.activeDuration, showZeroHours = true)

    val currentSpeed: String
        get() = formatSpeed(data.currentSpeed, SpeedUnit.KPH)       // FIXME: Use profile SpeedUnits

    val maxSpeed: String
        get() = formatSpeed(data.maxSpeed, SpeedUnit.KPH)           // FIXME: Use profile SpeedUnits

    val avgSpeed: String
        get() = formatSpeed(data.avgSpeed, SpeedUnit.KPH)           // FIXME: Use profile SpeedUnits
}
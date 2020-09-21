package ie.justonetech.roadtriptracker.utils

import android.content.Context
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.TrackingStats

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingStatsFormatter
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingStatsFormatter(context: Context, data: TrackingStats, private val speedUnit: SpeedUnit,
                             private val distanceUnit: DistanceUnit)
    : ModelFormatter<TrackingStats>(context, data) {

    val totalDuration: String
        get() = formatDuration(data.totalDuration, showZeroHours = true)

    val activeDuration: String
        get() = formatDuration(data.activeDuration, showZeroHours = true)

    val pausedDuration: String
        get() = formatDuration(data.totalDuration - data.activeDuration, showZeroHours = true)

    val distance: String
        get() = formatDistance(data.distance, distanceUnit, false)

    val currentSpeed: String
        get() = formatSpeed(data.currentSpeed, speedUnit, FormatUtils.SpeedFormat.FORMAT_WHOLE_NUMBER)

    val maxSpeed: String
        get() = formatSpeed(data.maxSpeed, speedUnit, FormatUtils.SpeedFormat.FORMAT_WHOLE_NUMBER)

    val avgSpeed: String
        get() = formatSpeed(data.avgSpeed, speedUnit, FormatUtils.SpeedFormat.FORMAT_WHOLE_NUMBER)

    val avgActiveSpeed: String
        get() = formatSpeed(data.avgActiveSpeed, speedUnit, FormatUtils.SpeedFormat.FORMAT_WHOLE_NUMBER)

    val distanceLabel: String
        get() = context.getString(R.string.tracking_stat_distance_with_unit_suffix, distanceUnit.getSuffix(context))

    val speedLabel: String
        get() = context.getString(R.string.tracking_stat_speed_with_unit_suffix, speedUnit.getSuffix(context))

    val climbLabel: String
        get() = context.getString(R.string.tracking_stat_total_climb_with_unit_suffix, DistanceUnit.METERS.getSuffix(context))
}
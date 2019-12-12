package ie.justonetech.roadtriptracker.utils

import android.content.Context
import ie.justonetech.roadtriptracker.model.RouteDetail


///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailFormatter
// Utility class use to format RouteDetail model data for display
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailFormatter(context: Context, data: RouteDetail) : ModelFormatter<RouteDetail>(context, data) {

    val startTime: String
        get() = FormatUtils().formatDate(data.startTime, format = FormatUtils.DateFormat.FORMAT_LONG_SHORT_TIME)

    val endTime: String
        get() = FormatUtils().formatDate(data.endTime, format = FormatUtils.DateFormat.FORMAT_LONG_SHORT_TIME)

    val distance: String
        get() = formatDistance(data.distance, data.profile.distanceUnit)

    val climb: String
        get() = formatDistance(data.maxClimb, DistanceUnit.METERS)

    val totalDuration: String
        get() = formatDuration(data.totalDuration)

    val activeDuration: String
        get() = formatDuration(data.activeDuration)

    val pausedDuration: String
        get() = formatDuration(data.pausedDuration)

    val maxSpeed: String
        get() = formatSpeed(data.maxSpeed, data.profile.speedUnit)

    val avgSpeed: String
        get() = formatSpeed(data.avgSpeed, data.profile.speedUnit)

    val pace: String
        get() = "<TODO>"

    val profileName: String
        get() = ProfileType.fromId(data.profileId).getName(context)
}
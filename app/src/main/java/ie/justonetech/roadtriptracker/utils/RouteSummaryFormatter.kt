package ie.justonetech.roadtriptracker.utils

import android.content.Context
import ie.justonetech.roadtriptracker.model.RouteSummary

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummaryFormatter
// Utility class use to format RouteSummary model data for display
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteSummaryFormatter(context: Context, data: RouteSummary) : ModelFormatter<RouteSummary>(context, data) {

    val startTime: String
        get() = FormatUtils().formatDate(data.startTime, format = FormatUtils.DateFormat.FORMAT_SHORT_SHORT_TIME)

    val endTime: String
        get() = FormatUtils().formatDate(data.endTime, format = FormatUtils.DateFormat.FORMAT_SHORT_SHORT_TIME)

    val totalDuration: String
        get() = formatDuration(data.totalDuration)

    val distance: String
        get() = formatDistance(data.distance, data.profileConfig.distanceUnit)

    val avgSpeed: String
        get() = formatSpeed(data.avgActiveSpeed, data.profileConfig.speedUnit)

    val profileName: String
        get() = ProfileType.fromId(data.profileId).getName(context)

    val profileColor: Int
        get() = ProfileType.fromId(data.profileId).getColor(context)
}
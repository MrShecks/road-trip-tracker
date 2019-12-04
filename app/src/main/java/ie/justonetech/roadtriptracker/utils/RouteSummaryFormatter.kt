package ie.justonetech.roadtriptracker.utils

import android.content.Context
import ie.justonetech.roadtriptracker.model.RouteSummary

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummaryFormatter
// Utility class use to format RouteSummary model data for display
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteSummaryFormatter(context: Context, data: RouteSummary) : ModelFormatter<RouteSummary>(context, data) {

    val startTime: String
        get() = FormatUtils().formatDate(data.startTime, format = FormatUtils.DateFormat.FORMAT_LONG_SHORT_TIME)

    val endTime: String
        get() = FormatUtils().formatDate(data.endTime, format = FormatUtils.DateFormat.FORMAT_LONG_SHORT_TIME)
}
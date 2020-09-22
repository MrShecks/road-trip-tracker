/*
 * This file is part of Road Trip Tracker.
 *
 * Road Trip Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Road Trip Tracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Road Trip Tracker.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        get() = formatSpeed(data.avgSpeed, data.profileConfig.speedUnit)

    val profileName: String
        get() = ProfileType.fromId(data.profileId).getName(context)

    val profileColor: Int
        get() = ProfileType.fromId(data.profileId).getColor(context)
}
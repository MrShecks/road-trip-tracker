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
import ie.justonetech.roadtriptracker.model.RouteDetail


///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailFormatter
// Utility class use to format RouteDetail model data for display
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailFormatter(context: Context, data: RouteDetail) : ModelFormatter<RouteDetail>(context, data) {

    val startTime: String
        get() = FormatUtils().formatDate(data.startTime, format = FormatUtils.DateFormat.FORMAT_SHORT_SHORT_TIME)

    val endTime: String
        get() = FormatUtils().formatDate(data.endTime, format = FormatUtils.DateFormat.FORMAT_SHORT_SHORT_TIME)

    val distance: String
        get() = formatDistance(data.distance, data.profileConfig.distanceUnit)

    val totalDuration: String
        get() = formatDuration(data.totalDuration)

    val activeDuration: String
        get() = formatDuration(data.activeDuration)

    val pausedDuration: String
        get() = formatDuration(data.pausedDuration)

    val maxSpeed: String
        get() = formatSpeed(data.maxSpeed, data.profileConfig.speedUnit)

    val avgSpeed: String
        get() = formatSpeed(data.avgSpeed, data.profileConfig.speedUnit)

    val pace: String
        get() {
            return String.format(
                "%s / %s",
                formatDuration((data.activeDuration / DistanceUnit.METERS.convertTo(data.distance, data.profileConfig.distanceUnit)).toLong()),
                data.profileConfig.distanceUnit.getSuffix(context)
            )
        }

    // FIXME: Does it make sense to make the elevation distance metric configurable?
    val maxElevationGain: String
        get() = formatDistance(data.maxElevationGain, DistanceUnit.METERS)

    val totalElevationGain: String
        get() = formatDistance(data.totalElevationGain, DistanceUnit.METERS)

    val profileName: String
        get() = ProfileType.fromId(data.profileId).getName(context)

    val profileColor: Int
        get() = ProfileType.fromId(data.profileId).getColor(context)
}
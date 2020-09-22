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

    val distanceLabel: String
        get() = context.getString(R.string.tracking_stat_distance_with_unit_suffix, distanceUnit.getSuffix(context))

    val speedLabel: String
        get() = context.getString(R.string.tracking_stat_speed_with_unit_suffix, speedUnit.getSuffix(context))

    val climbLabel: String
        get() = context.getString(R.string.tracking_stat_total_climb_with_unit_suffix, DistanceUnit.METERS.getSuffix(context))
}
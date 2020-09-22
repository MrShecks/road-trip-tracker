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
import java.util.concurrent.TimeUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// ModelFormatter
// Base class containing common utility function for formatting model data
///////////////////////////////////////////////////////////////////////////////////////////////////

open class ModelFormatter<T>(protected val context: Context, val data: T) {

    protected fun formatDuration(duration: Long, sourceUnits: TimeUnit = TimeUnit.MILLISECONDS, showZeroHours: Boolean = false): String {
        return FormatUtils().formatDuration(duration, sourceUnits, showZeroHours)
    }

    protected fun formatDistance(distance: Double, targetUnit: DistanceUnit, withSuffix: Boolean = true): String {
        return FormatUtils().formatDistance(context, distance, DistanceUnit.METERS, targetUnit, withSuffix)
    }

    protected fun formatSpeed(speed: Float, targetUnit: SpeedUnit, format: FormatUtils.SpeedFormat = FormatUtils.SpeedFormat.FORMAT_WHOLE_NUMBER_WITH_SUFFIX): String {
        return FormatUtils().formatSpeed(context, speed, SpeedUnit.MPS, targetUnit, format)
    }
}
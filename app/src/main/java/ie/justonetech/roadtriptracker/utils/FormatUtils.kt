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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// FormatUtils
// Singleton class to provide formatting utilities and cached formatters for different raw types
// (dates, durations etc) used throughout the application
///////////////////////////////////////////////////////////////////////////////////////////////////

class FormatUtils private constructor() {

    enum class DateFormat {
        FORMAT_SHORT_TEXT,
        FORMAT_DATABASE,
        FORMAT_LONG_SHORT_TIME,
        FORMAT_SHORT_SHORT_TIME
    }

    enum class SpeedFormat {
        FORMAT_DECIMAL,
        FORMAT_WHOLE_NUMBER,
        FORMAT_DECIMAL_WITH_SUFFIX,
        FORMAT_WHOLE_NUMBER_WITH_SUFFIX
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val dateFormats = mapOf(
        DateFormat.FORMAT_SHORT_TEXT to SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault()),
        DateFormat.FORMAT_DATABASE to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
        DateFormat.FORMAT_LONG_SHORT_TIME to SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()),
        DateFormat.FORMAT_SHORT_SHORT_TIME to SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    )

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    fun formatDate(date: Date?, defaultValue: String = "", format: DateFormat = DateFormat.FORMAT_SHORT_TEXT): String {
        synchronized(LOCK) {
            return date?.let {
                dateFormats[format]?.format(date) ?: defaultValue
            } ?: defaultValue
        }
    }

    fun parseDate(str: String, defaultValue: Date? = null, format: DateFormat = DateFormat.FORMAT_SHORT_TEXT): Date? {
        synchronized(LOCK) {
            return try {
                dateFormats[format]?.parse(str)
            } catch (e: ParseException) {
                defaultValue
            }
        }
    }

    fun formatDuration(duration: Long, sourceUnit: TimeUnit, showZeroHours: Boolean = false): String {
        val hours = sourceUnit.toHours(duration)
        val minutes = sourceUnit.toMinutes(duration) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = sourceUnit.toSeconds(duration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes)

        return if(hours > 0 || showZeroHours)
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        else
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    fun formatDistance(context: Context, distance: Double, sourceUnit: DistanceUnit, targetUnit: DistanceUnit, withSuffix: Boolean = true): String {
        return if(withSuffix)
            String.format(Locale.getDefault(), "%.2f %s", sourceUnit.convertTo(distance, targetUnit), targetUnit.getSuffix(context))
        else
            String.format(Locale.getDefault(), "%.2f", sourceUnit.convertTo(distance, targetUnit))
    }

    fun formatSpeed(context: Context, speed: Float, sourceUnit: SpeedUnit, targetUnit: SpeedUnit, format: SpeedFormat = SpeedFormat.FORMAT_WHOLE_NUMBER_WITH_SUFFIX): String {
        val targetSpeed = sourceUnit.convertTo(speed, targetUnit)

        return when(format) {
            SpeedFormat.FORMAT_DECIMAL -> String.format(Locale.getDefault(), "%.2f", targetSpeed)
            SpeedFormat.FORMAT_WHOLE_NUMBER -> String.format(Locale.getDefault(), "%d", targetSpeed.toInt())
            SpeedFormat.FORMAT_DECIMAL_WITH_SUFFIX -> String.format(Locale.getDefault(), "%.2f %s", targetSpeed, targetUnit.getSuffix(context))
            SpeedFormat.FORMAT_WHOLE_NUMBER_WITH_SUFFIX -> String.format(Locale.getDefault(), "%d %s", targetSpeed.toInt(), targetUnit.getSuffix(context))
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        @Volatile
        private var instance: FormatUtils? = null
        private val LOCK = Any()

        @JvmStatic
        operator fun invoke(): FormatUtils {
            return instance?: synchronized(LOCK) {
                instance?: FormatUtils().also {
                    instance = it
                }
            }
        }
    }
}
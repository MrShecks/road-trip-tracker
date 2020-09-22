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
import androidx.annotation.StringRes
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// SpeedUnit
// Supported speed units and conversion utilities
///////////////////////////////////////////////////////////////////////////////////////////////////

enum class SpeedUnit(val id: Int, @StringRes private val suffixId: Int) {

    // Note: The enum 'id' value is stored in application database
    // so care should be taken to keep the ids consistent when
    // adding or removing enums

    MPS(2000, R.string.speed_unit_suffix_mps) {
        override fun toMetersPerSecond(s: Float)    = s
        override fun toMilesPerHour(s: Float)       = s * MPS_TO_MPH
        override fun toKilometersPerHour(s: Float)  = s * MPS_TO_KPH
        override fun toFeetPerSecond(s: Float)      = s * MPS_TO_FPS
        override fun toKnots(s: Float)              = s * MPS_TO_KNOTS
    },

    MPH(2001, R.string.speed_unit_suffix_mph) {
        override fun toMetersPerSecond(s: Float)    = s * MPH_TO_MPS
        override fun toMilesPerHour(s: Float)       = s
        override fun toKilometersPerHour(s: Float)  = s * MPH_TO_KPH
        override fun toFeetPerSecond(s: Float)      = s * MPH_TO_FPS
        override fun toKnots(s: Float)              = s * MPH_TO_KNOTS
    },

    KPH(2003, R.string.speed_unit_suffix_kph) {
        override fun toMetersPerSecond(s: Float)    = s * KPH_TO_MPS
        override fun toMilesPerHour(s: Float)       = s * KPH_TO_MPH
        override fun toKilometersPerHour(s: Float)  = s
        override fun toFeetPerSecond(s: Float)      = s * KPH_TO_FPS
        override fun toKnots(s: Float)              = s * KPH_TO_KNOTS
    },

    FPS(2004, R.string.speed_unit_suffix_fps) {
        override fun toMetersPerSecond(s: Float)    = s * FPS_TO_MPS
        override fun toMilesPerHour(s: Float)       = s * FPS_TO_MPH
        override fun toKilometersPerHour(s: Float)  = s * FPS_TO_KPH
        override fun toFeetPerSecond(s: Float)      = s
        override fun toKnots(s: Float)              = s * FPS_TO_KNOTS
    },

    KNOTS(2005, R.string.speed_unit_suffix_knots) {
        override fun toMetersPerSecond(s: Float)    = s * KNOTS_TO_MPS
        override fun toMilesPerHour(s: Float)       = s * KNOTS_TO_MPH
        override fun toKilometersPerHour(s: Float)  = s * KNOTS_TO_KPH
        override fun toFeetPerSecond(s: Float)      = s * KNOTS_TO_FPS
        override fun toKnots(s: Float)              = s
    };

    abstract fun toMetersPerSecond(s: Float): Float
    abstract fun toMilesPerHour(s: Float): Float
    abstract fun toKilometersPerHour(s: Float): Float
    abstract fun toFeetPerSecond(s: Float): Float
    abstract fun toKnots(s: Float): Float

    fun convertTo(s: Float, targetUnit: SpeedUnit) = when(targetUnit) {
        MPS     -> toMetersPerSecond(s)
        MPH     -> toMilesPerHour(s)
        KPH     -> toKilometersPerHour(s)
        FPS     -> toFeetPerSecond(s)
        KNOTS   -> toKnots(s)
    }

    fun getSuffix(context: Context): String = context.getString(suffixId)

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private const val MPS_TO_MPH    = 2.2369363f
        private const val MPS_TO_KPH    = 3.6f
        private const val MPS_TO_FPS    = 3.2808399f
        private const val MPS_TO_KNOTS  = 1.9438445f

        private const val MPH_TO_MPS    = 0.44704f
        private const val MPH_TO_KPH    = 1.609344f
        private const val MPH_TO_FPS    = 1.4666667f
        private const val MPH_TO_KNOTS  = 0.86897624f

        private const val KPH_TO_MPS    = 0.27777778f
        private const val KPH_TO_MPH    = 0.62137119f
        private const val KPH_TO_FPS    = 0.91134442f
        private const val KPH_TO_KNOTS  = 0.5399568f

        private const val FPS_TO_MPS    = 0.3048f
        private const val FPS_TO_MPH    = 0.68181818f
        private const val FPS_TO_KPH    = 1.09728f
        private const val FPS_TO_KNOTS  = 0.5924838f

        private const val KNOTS_TO_MPS  = 0.51444444f
        private const val KNOTS_TO_MPH  = 1.1507794f
        private const val KNOTS_TO_KPH  = 1.852f
        private const val KNOTS_TO_FPS  = 1.6878099f

        @JvmStatic
        fun fromId(id: Int, defaultValue: SpeedUnit = KPH): SpeedUnit {
            return values().firstOrNull { it.id == id } ?: defaultValue
        }
    }
}
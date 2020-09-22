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

package ie.justonetech.roadtriptracker.model

import java.util.*

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingStats
/////////////////////////////////////////////////////////////////////////////////////////////////////////

data class TrackingStats(
    val startTimestamp: Date?,
    val totalDuration: Long,
    val activeDuration: Long,

    val distance: Double,

    val maxSpeed: Float,
    val avgSpeed: Float,
    val currentSpeed: Float,

    val maxElevationGain: Double,
    val totalElevationGain: Double
) {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private constructor() : this(
        null,
        0,
        0,
        0.0,
        0f,
        0f,
        0f,
        0.0,
        0.0
    )

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        val EMPTY = TrackingStats()
    }
}
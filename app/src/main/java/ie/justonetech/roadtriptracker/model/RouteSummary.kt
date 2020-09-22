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

import androidx.room.ColumnInfo
import androidx.room.Relation
import ie.justonetech.roadtriptracker.model.db.entities.DbProfileConfig
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummary
// Model class containing a summary representation of a route
///////////////////////////////////////////////////////////////////////////////////////////////////

data class RouteSummary(

    @ColumnInfo(name = "_id")                   val id: Int?,
    @ColumnInfo(name = "profile_id")            var profileId: Int,

    @ColumnInfo(name = "start_time")            var startTime: Date,
    @ColumnInfo(name = "end_time")              var endTime: Date,

    @ColumnInfo(name = "total_duration")        var totalDuration: Long,
    @ColumnInfo(name = "active_duration")       var activeDuration: Long,

    @ColumnInfo(name = "distance")              var distance: Double,
    @ColumnInfo(name = "max_speed")             var maxSpeed: Float,
    @ColumnInfo(name = "avg_speed")             var avgSpeed: Float,

    @ColumnInfo(name = "max_elevation_gain")    val maxElevationGain: Float,
    @ColumnInfo(name = "total_elevation_gain")  val totalElevationGain: Float,

    @Relation(parentColumn = "profile_id", entityColumn = "_id", entity = DbProfileConfig::class)
    val profileConfig: ProfileConfig
)

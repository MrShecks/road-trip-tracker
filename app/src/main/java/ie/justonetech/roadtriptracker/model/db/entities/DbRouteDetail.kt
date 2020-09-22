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

package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.*
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbRouteDetail
// Represents the header details for a tracked route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(
    tableName = "TRouteDetail",
    indices = [
        Index(value = ["profile_id"], name = "IDX_RouteDetail_Profile_Id")
    ],
    foreignKeys = [
        ForeignKey(entity = DbProfileConfig::class, parentColumns = ["_id"], childColumns = ["profile_id"], onDelete = ForeignKey.NO_ACTION)
    ]
)
data class DbRouteDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")                   val id: Int?,
    @ColumnInfo(name = "profile_id")            val profileId: Int,

    @ColumnInfo(name = "start_time")            val startTime: Date,
    @ColumnInfo(name = "end_time")              val endTime: Date,

    @ColumnInfo(name = "total_duration")        val totalDuration: Long,
    @ColumnInfo(name = "active_duration")       val activeDuration: Long,

    @ColumnInfo(name = "distance")              val distance: Double,
    @ColumnInfo(name = "max_elevation_gain")    val maxElevationGain: Double,
    @ColumnInfo(name = "total_elevation_gain")  val totalElevationGain: Double,

    @ColumnInfo(name = "max_speed")             val maxSpeed: Float,
    @ColumnInfo(name = "avg_speed")             val avgSpeed: Float
)

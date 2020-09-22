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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.SpeedUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbProfileConfig
// Profile configuration for a route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(tableName = "TProfileConfig")
data class DbProfileConfig(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")                   val id: Int,

    @ColumnInfo(name = "distance_unit")         val distanceUnit: DistanceUnit,
    @ColumnInfo(name = "speed_unit")            val speedUnit: SpeedUnit,
    @ColumnInfo(name = "sample_interval")       val sampleInterval: Float,
    @ColumnInfo(name = "stat_update_interval")  val statUpdateInterval: Long
)


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

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbRoutePoint
// Represents a single location point on a route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(
    tableName = "TRoutePoint",
    indices = [
        Index(value = ["route_id"], name = "IDX_TRoutePoint_route_id")
    ],
    foreignKeys = [
        ForeignKey(entity = DbRouteDetail::class, parentColumns = ["_id"], childColumns = ["route_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbRoutePoint(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")               val id: Int?,
    @ColumnInfo(name = "route_id")          val routeId: Int,

    @ColumnInfo(name = "timestamp")         var timeStamp: Long,

    @ColumnInfo(name = "latitude")          var latitude: Double,
    @ColumnInfo(name = "longitude")         var longitude: Double,
    @ColumnInfo(name = "altitude")          var altitude: Double,

    @ColumnInfo(name = "speed")             var speed: Float,
    @ColumnInfo(name = "bearing")           var bearing: Float,

    @ColumnInfo(name = "barometric_alt")    var barometricAltitude: Double
)

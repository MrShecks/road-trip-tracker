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

package ie.justonetech.roadtriptracker.model.db.dao

import androidx.room.Dao
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint

///////////////////////////////////////////////////////////////////////////////////////////////////
// RoutePointDao
// Data Access Object for DbRoutePoint
///////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface RoutePointDao : BaseDao<DbRoutePoint>  {
    // FIXME: Delete this, it probably won't be needed since the route points are included in RouteDetail via @Relation
}
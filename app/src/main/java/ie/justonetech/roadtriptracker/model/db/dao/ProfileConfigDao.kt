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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.db.entities.DbProfileConfig

///////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileConfigDao
// Data Access Object for DbProfileConfig
///////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface ProfileConfigDao : BaseDao<DbProfileConfig> {

    @Transaction
    @Query("SELECT * FROM TProfileConfig ORDER BY _id ASC")
    fun getList(): LiveData<List<ProfileConfig>>

    @Query("SELECT * FROM TProfileConfig WHERE _id=:id LIMIT 1")
    fun getProfileById(id: Int): LiveData<ProfileConfig>
}
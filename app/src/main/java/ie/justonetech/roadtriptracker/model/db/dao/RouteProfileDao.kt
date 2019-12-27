package ie.justonetech.roadtriptracker.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteProfile

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteProfileDao
// Data Access Object for DbRouteProfile
///////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface RouteProfileDao : BaseDao<DbRouteProfile> {

    @Transaction
    @Query("SELECT * FROM TRouteProfile ORDER BY _id ASC")
    fun getList(): LiveData<List<ProfileConfig>>

    @Query("SELECT * FROM TRouteProfile WHERE _id=:id LIMIT 1")
    fun getProfileById(id: Int): LiveData<ProfileConfig>
}
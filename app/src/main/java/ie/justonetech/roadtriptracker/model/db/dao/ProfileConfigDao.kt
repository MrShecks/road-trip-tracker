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
package ie.justonetech.roadtriptracker.model.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.justonetech.roadtriptracker.model.RouteDetail
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteDetail

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailDao
// Data Access Object for DbRouteDetail
///////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface RouteDetailDao : BaseDao<DbRouteDetail> {

    @Transaction
    @Query("SELECT * FROM TRouteDetail ORDER BY _id DESC")
    fun getList(): DataSource.Factory<Int, RouteSummary>

    @Transaction
    @Query("SELECT * FROM TRouteDetail WHERE _id=:id LIMIT 1")
    fun getById(id: Int): LiveData<RouteDetail>

    @Query("DELETE FROM TRouteDetail WHERE _id=:id")
    fun deleteById(id: Int): Int
}
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
package ie.justonetech.roadtriptracker.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.google.android.gms.maps.model.LatLng
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteProfile
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetail
// Model class containing a complete representation of a route
///////////////////////////////////////////////////////////////////////////////////////////////////

data class RouteDetail(

    @ColumnInfo(name = "_id")               val id: Int?,
    @ColumnInfo(name = "profile_id")        var profileId: Int,

    @ColumnInfo(name = "start_time")        var startTime: Date,
    @ColumnInfo(name = "end_time")          var endTime: Date,

    @ColumnInfo(name = "total_duration")    var totalDuration: Long,
    @ColumnInfo(name = "active_duration")   var activeDuration: Long,

    @ColumnInfo(name = "distance")          var distance: Double,
    @ColumnInfo(name = "max_speed")         var maxSpeed: Float,
    @ColumnInfo(name = "avg_speed")         var avgSpeed: Float,
    @ColumnInfo(name = "max_climb")         var maxClimb: Double,

    @Relation(parentColumn = "profile_id", entityColumn = "_id", entity = DbRouteProfile::class)
    val profile: RouteProfile,

    @Relation(parentColumn = "_id", entityColumn = "route_id", entity = DbRoutePoint::class)
    val points: List<Point>

) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Point
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    data class Point (
        @ColumnInfo(name = "_id")               val id: Int?,
        @ColumnInfo(name = "timestamp")         var timeStamp: Date,

        @ColumnInfo(name = "latitude")          var latitude: Double,
        @ColumnInfo(name = "longitude")         var longitude: Double,
        @ColumnInfo(name = "altitude")          var altitude: Double,

        @ColumnInfo(name = "speed")             var speed: Float,
        @ColumnInfo(name = "bearing")           var bearing: Float,
        @ColumnInfo(name = "accuracy")          var accuracy: Float
    ) {

        val latLng: LatLng
            get() = LatLng(latitude, longitude)
    }

}

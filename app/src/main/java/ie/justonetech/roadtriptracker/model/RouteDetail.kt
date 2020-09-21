package ie.justonetech.roadtriptracker.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.google.android.gms.maps.model.LatLng
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.model.db.entities.DbProfileConfig
import ie.justonetech.roadtriptracker.utils.ProfileType
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetail
// Model class containing a complete representation of a route
///////////////////////////////////////////////////////////////////////////////////////////////////

data class RouteDetail(

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
    @ColumnInfo(name = "avg_speed")             val avgSpeed: Float,

    @ColumnInfo(name = "is_favourite")          val isFavourite: Boolean,

    @Relation(parentColumn = "profile_id", entityColumn = "_id", entity = DbProfileConfig::class)
    val profileConfig: ProfileConfig,

    @Relation(parentColumn = "_id", entityColumn = "route_id", entity = DbRoutePoint::class)
    val points: List<Point>

) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    val profileType
        get() = ProfileType.fromId(profileId)

    val pausedDuration
        get() = totalDuration - activeDuration

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Point
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    data class Point (
        @ColumnInfo(name = "_id")               val id: Int?,
        @ColumnInfo(name = "timestamp")         val timeStamp: Long,

        @ColumnInfo(name = "latitude")          val latitude: Double,
        @ColumnInfo(name = "longitude")         val longitude: Double,
        @ColumnInfo(name = "altitude")          val altitude: Double,

        @ColumnInfo(name = "speed")             val speed: Float,
        @ColumnInfo(name = "bearing")           val bearing: Float,

        @ColumnInfo(name = "barometric_alt")    var barometricAltitude: Double

    ) {

        val latLng: LatLng
            get() = LatLng(latitude, longitude)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        const val INVALID_ID: Int = -1
    }
}
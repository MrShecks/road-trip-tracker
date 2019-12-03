package ie.justonetech.roadtriptracker.model

import androidx.room.ColumnInfo
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummary
// Model class containing a summary representation of a route
///////////////////////////////////////////////////////////////////////////////////////////////////

data class RouteSummary(

    @ColumnInfo(name = "_id")               val id: Int?,
    @ColumnInfo(name = "start_time")        var startTime: Date,
    @ColumnInfo(name = "end_time")          var endTime: Date,

    @ColumnInfo(name = "total_duration")    var totalDuration: Long,
    @ColumnInfo(name = "active_duration")   var activeDuration: Long,

    @ColumnInfo(name = "distance")          var distance: Double,
    @ColumnInfo(name = "max_speed")         var maxSpeed: Float,
    @ColumnInfo(name = "avg_speed")         var avgSpeed: Float,
    @ColumnInfo(name = "max_climb")         var maxClimb: Double
)

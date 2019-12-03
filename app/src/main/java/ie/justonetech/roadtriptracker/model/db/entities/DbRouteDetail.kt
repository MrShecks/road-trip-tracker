package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbRouteDetail
// Represents the header details for a tracked route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(tableName = "TRouteDetail")
data class DbRouteDetail(
    @PrimaryKey(autoGenerate = true)
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

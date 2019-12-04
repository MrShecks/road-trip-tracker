package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbRouteDetail
// Represents the header details for a tracked route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(
    tableName = "TRouteDetail",
    indices = [
        androidx.room.Index(value = ["profile_id"], name = "IDX_RouteDetail_Profile_Id")
    ],
    foreignKeys = [
        ForeignKey(entity = DbRouteProfile::class, parentColumns = ["_id"], childColumns = ["profile_id"], onDelete = ForeignKey.NO_ACTION)
    ]
)
data class DbRouteDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")               val id: Int?,
    @ColumnInfo(name = "profile_id")        val profileId: Int,

    @ColumnInfo(name = "start_time")        var startTime: Date,
    @ColumnInfo(name = "end_time")          var endTime: Date,

    @ColumnInfo(name = "total_duration")    var totalDuration: Long,
    @ColumnInfo(name = "active_duration")   var activeDuration: Long,

    @ColumnInfo(name = "distance")          var distance: Double,
    @ColumnInfo(name = "max_speed")         var maxSpeed: Float,
    @ColumnInfo(name = "avg_speed")         var avgSpeed: Float,
    @ColumnInfo(name = "max_climb")         var maxClimb: Double
)

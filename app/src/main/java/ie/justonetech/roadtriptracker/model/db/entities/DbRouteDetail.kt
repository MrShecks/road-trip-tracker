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
    @ColumnInfo(name = "_id")                   val id: Int?,
    @ColumnInfo(name = "profile_id")            val profileId: Int,

    @ColumnInfo(name = "start_time")            val startTime: Date,
    @ColumnInfo(name = "end_time")              val endTime: Date,

    @ColumnInfo(name = "total_duration")        val totalDuration: Long,
    @ColumnInfo(name = "active_duration")       val activeDuration: Long,

    @ColumnInfo(name = "distance")              val distance: Double,
    @ColumnInfo(name = "max_speed")             val maxSpeed: Float,
    @ColumnInfo(name = "avg_speed")             val avgSpeed: Float,
    @ColumnInfo(name = "avg_active_speed")      val avgActiveSpeed: Float,

    @ColumnInfo(name = "is_favourite")          val isFavourite: Boolean,

    // FIXME: OCD - Move these columns before is_favourite next time the DB is dropped
    @ColumnInfo(name = "max_elevation_gain")    val maxElevationGain: Float,
    @ColumnInfo(name = "total_elevation_gain")  val totalElevationGain: Float
)

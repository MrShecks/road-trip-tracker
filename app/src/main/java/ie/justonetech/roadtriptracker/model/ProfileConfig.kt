package ie.justonetech.roadtriptracker.model

import androidx.room.ColumnInfo
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.SpeedUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileConfig
// Model class containing profile configuration settings for a route
///////////////////////////////////////////////////////////////////////////////////////////////////

data class ProfileConfig(
    @ColumnInfo(name = "_id")                   val id: Int,

    @ColumnInfo(name = "distance_unit")         val distanceUnit: DistanceUnit,
    @ColumnInfo(name = "speed_unit")            val speedUnit: SpeedUnit,
    @ColumnInfo(name = "sample_interval")       val sampleInterval: Float,
    @ColumnInfo(name = "stat_update_interval")  val statUpdateInterval: Long
)

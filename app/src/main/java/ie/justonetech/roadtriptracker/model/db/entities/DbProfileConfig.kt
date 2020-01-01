package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.SpeedUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbProfileConfig
// Profile configuration for a route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(tableName = "TProfileConfig")
data class DbProfileConfig(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")                   val id: Int,

    @ColumnInfo(name = "distance_unit")         val distanceUnit: DistanceUnit,
    @ColumnInfo(name = "speed_unit")            val speedUnit: SpeedUnit,
    @ColumnInfo(name = "sample_interval")       val sampleInterval: Float,
    @ColumnInfo(name = "stat_update_interval")  val statUpdateInterval: Long
)


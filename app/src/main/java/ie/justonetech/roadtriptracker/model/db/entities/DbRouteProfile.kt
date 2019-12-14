package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.SpeedUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// TRouteProfile
// Profile configuration for a route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(tableName = "TRouteProfile")
data class DbRouteProfile(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")               val id: Int,

    @ColumnInfo(name = "tag_color")         val tagColor: Int,
    @ColumnInfo(name = "distance_unit")     val distanceUnit: DistanceUnit,
    @ColumnInfo(name = "speed_unit")        val speedUnit: SpeedUnit
)


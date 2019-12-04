package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

///////////////////////////////////////////////////////////////////////////////////////////////////
// TRouteProfile
// Profile configuration for a route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(tableName = "TRouteProfile")
data class DbRouteProfile(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")               val id: Int,

    @ColumnInfo(name = "accent_color")      val accentColor: Long,
    @ColumnInfo(name = "distance_unit")     val distanceUnit: Int,
    @ColumnInfo(name = "speed_unit")        val speedUnit: Int
)


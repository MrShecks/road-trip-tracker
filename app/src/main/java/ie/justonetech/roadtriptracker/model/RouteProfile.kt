package ie.justonetech.roadtriptracker.model

import androidx.room.ColumnInfo

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteProfile
// Model class containing profile configuration settings for a route
///////////////////////////////////////////////////////////////////////////////////////////////////

data class RouteProfile(
    @ColumnInfo(name = "_id")               val id: Int,

    @ColumnInfo(name = "accent_color")      val accentColor: Long,
    @ColumnInfo(name = "distance_unit")     val distanceUnit: Int,
    @ColumnInfo(name = "speed_unit")        val speedUnit: Int
)

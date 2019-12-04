package ie.justonetech.roadtriptracker.model.db.entities

import androidx.room.*
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// DbRoutePoint
// Represents a single location point on a route
///////////////////////////////////////////////////////////////////////////////////////////////////

@Entity(
    tableName = "TRoutePoint",
    indices = [
        Index(value = ["route_id"], name = "IDX_route_id")
    ],
    foreignKeys = [
        ForeignKey(entity = DbRouteDetail::class, parentColumns = ["_id"], childColumns = ["route_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbRoutePoint(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")               val id: Int?,
    @ColumnInfo(name = "route_id")          val routeId: Int,

    @ColumnInfo(name = "timestamp")         var timeStamp: Date,

    @ColumnInfo(name = "latitude")          var latitude: Double,
    @ColumnInfo(name = "longitude")         var longitude: Double,
    @ColumnInfo(name = "altitude")          var altitude: Double,

    @ColumnInfo(name = "speed")             var speed: Float,
    @ColumnInfo(name = "bearing")           var bearing: Float,
    @ColumnInfo(name = "accuracy")          var accuracy: Float
)

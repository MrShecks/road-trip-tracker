package ie.justonetech.roadtriptracker.model.db

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import ie.justonetech.roadtriptracker.model.db.dao.RouteDetailDao
import ie.justonetech.roadtriptracker.model.db.dao.RoutePointDao
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteDetail
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.utils.FormatUtils
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingDatabase
// Implementation of Room database used to save tracked route information
///////////////////////////////////////////////////////////////////////////////////////////////////

private const val DATABASE_VERSION = 1

///////////////////////////////////////////////////////////////////////////////////////////////////

@Database(
    entities = [
        DbRouteDetail::class,
        DbRoutePoint::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)

@TypeConverters(
    DateTypeConverter::class
)

abstract class TrackingDatabase protected constructor() : RoomDatabase() {

    abstract fun routeDetailDao(): RouteDetailDao
    abstract fun routePointDao(): RoutePointDao

    companion object {
        private val TAG = TrackingDatabase::class.java.simpleName

        private const val DATABASE_NAME = "RoadTripTracker.db"

        @Volatile
        private var instance: TrackingDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context): TrackingDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrackingDatabase::class.java,
                    DATABASE_NAME

                )
                .addCallback(onCreateCallback)
                .build().also { instance = it }
            }
        }

        private val onCreateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                Log.d(TAG, "onCreate() Called on thread: ${Thread.currentThread()}")

                // TODO: Add any db initialisation SQL scripts here...
            }
        }
    }
}


///////////////////////////////////////////////////////////////////////////////////////////////////
// DateTypeConverter
// Covert the Java Date type to and from a string suitable for storage in the database
///////////////////////////////////////////////////////////////////////////////////////////////////

object DateTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toString(date: Date?): String {
        return FormatUtils().formatDate(date, format = FormatUtils.DateFormat.FORMAT_DATABASE)
    }

    @TypeConverter
    @JvmStatic
    fun fromString(str: String): Date? {
        return FormatUtils().parseDate(str, format= FormatUtils.DateFormat.FORMAT_DATABASE)
    }
}
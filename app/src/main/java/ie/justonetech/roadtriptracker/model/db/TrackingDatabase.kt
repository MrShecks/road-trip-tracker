package ie.justonetech.roadtriptracker.model.db

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import ie.justonetech.roadtriptracker.model.db.dao.RouteDetailDao
import ie.justonetech.roadtriptracker.model.db.dao.RoutePointDao
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteDetail
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteProfile
import ie.justonetech.roadtriptracker.utils.FormatUtils
import ie.justonetech.roadtriptracker.utils.ProfileType
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
        DbRoutePoint::class,
        DbRouteProfile::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)

@TypeConverters(
    DateTypeConverter::class
)

abstract class TrackingDatabase protected constructor()
    : RoomDatabase() {

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

                //
                // TODO: Add any db initialisation SQL scripts here...
                //

                //
                // Add static reference data for default route profile configurations
                //

                db.execSQL("INSERT INTO TRouteProfile VALUES(${ProfileType.PROFILE_TYPE_WALKING.id}, 0xf9aa33, 0, 0)")
                db.execSQL("INSERT INTO TRouteProfile VALUES(${ProfileType.PROFILE_TYPE_RUNNING.id}, 0xf9aa33, 0, 0)")
                db.execSQL("INSERT INTO TRouteProfile VALUES(${ProfileType.PROFILE_TYPE_CYCLING.id}, 0xf9aa33, 0, 0)")
                db.execSQL("INSERT INTO TRouteProfile VALUES(${ProfileType.PROFILE_TYPE_DRIVING.id}, 0xf9aa33, 0, 0)")
                db.execSQL("INSERT INTO TRouteProfile VALUES(${ProfileType.PROFILE_TYPE_BOATING.id}, 0xf9aa33, 0, 0)")
                db.execSQL("INSERT INTO TRouteProfile VALUES(${ProfileType.PROFILE_TYPE_MOTORCYCLING.id}, 0xf9aa33, 0, 0)")

                //
                // DEBUG: Adding some test routes
                //

                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_WALKING.id}, '2019-11-28T12:00:00.000', '2019-11-28T12:05:00.000', ${60 * 5}, ${60 * 5}, 25.5, 10, 10, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_WALKING.id}, '2019-11-28T13:00:00.000', '2019-11-28T13:10:00.000', ${60 * 10}, ${60 * 10}, 35.5, 15, 12, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_RUNNING.id}, '2019-11-29T14:00:00.000', '2019-11-28T14:15:00.000', ${60 * 15}, ${60 * 15}, 45.5, 25, 16, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_RUNNING.id}, '2019-11-29T15:00:00.000', '2019-11-28T15:20:00.000', ${60 * 20}, ${60 * 20}, 55.5, 30, 18, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_DRIVING.id}, '2019-11-30T16:00:00.000', '2019-11-28T16:25:00.000', ${60 * 25}, ${60 * 25}, 65.5, 35, 15, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_DRIVING.id}, '2019-11-30T17:00:00.000', '2019-11-28T17:30:00.000', ${60 * 30}, ${60 * 30}, 75.5, 40, 30, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_BOATING.id}, '2019-11-31T18:00:00.000', '2019-11-28T18:35:00.000', ${60 * 35}, ${60 * 35}, 85.5, 45, 28, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_BOATING.id}, '2019-11-31T19:00:00.000', '2019-11-28T19:40:00.000', ${60 * 40}, ${60 * 40}, 95.5, 50, 48, 0)")

                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_MOTORCYCLING.id}, '2019-12-01T12:00:00.000', '2019-12-28T12:05:00.000', ${60 * 5}, ${60 * 5}, 20, 10, 10, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_MOTORCYCLING.id}, '2019-12-01T13:00:00.000', '2019-12-28T13:10:00.000', ${60 * 10}, ${60 * 10}, 30, 30, 30, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_DRIVING.id}, '2019-12-02T14:00:00.000', '2019-12-28T14:15:00.000', ${60 * 15}, ${60 * 15}, 40, 40, 40, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_DRIVING.id}, '2019-12-02T15:00:00.000', '2019-12-28T15:20:00.000', ${60 * 20}, ${60 * 20}, 50, 50, 50, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_BOATING.id}, '2019-12-03T16:00:00.000', '2019-12-28T16:25:00.000', ${60 * 25}, ${60 * 25}, 60, 60, 60, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_BOATING.id}, '2019-12-03T17:00:00.000', '2019-12-28T17:30:00.000', ${60 * 30}, ${60 * 30}, 70, 70, 70, 0)")
                db.execSQL("INSERT INTO TRouteDetail VALUES(NULL, ${ProfileType.PROFILE_TYPE_CYCLING.id}, '2019-12-04T18:00:00.000', '2019-12-28T18:35:00.000', ${60 * 35}, ${60 * 35}, 10, 80, 80, 0)")
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
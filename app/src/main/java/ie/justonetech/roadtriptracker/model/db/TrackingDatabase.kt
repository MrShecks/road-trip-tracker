/*
 * This file is part of Road Trip Tracker.
 *
 * Road Trip Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Road Trip Tracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Road Trip Tracker.  If not, see <https://www.gnu.org/licenses/>.
 */

package ie.justonetech.roadtriptracker.model.db

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import ie.justonetech.roadtriptracker.model.db.dao.RouteDetailDao
import ie.justonetech.roadtriptracker.model.db.dao.RoutePointDao
import ie.justonetech.roadtriptracker.model.db.dao.ProfileConfigDao
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteDetail
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.model.db.entities.DbProfileConfig
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.FormatUtils
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.utils.SpeedUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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
        DbProfileConfig::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)

@TypeConverters(
    DateTypeConverter::class,
    SpeedUnitTypeConverter::class,
    DistanceUnitTypeConverter::class
)

abstract class TrackingDatabase protected constructor()
    : RoomDatabase() {

    abstract fun routeDetailDao(): RouteDetailDao
    abstract fun routePointDao(): RoutePointDao
    abstract fun profileConfigDao(): ProfileConfigDao

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

                db.execSQL("INSERT INTO TProfileConfig VALUES(${ProfileType.PROFILE_TYPE_WALKING.id}, ${DistanceUnit.KILOMETERS.id}, ${SpeedUnit.KPH.id}, 1.0, 1000)")
                db.execSQL("INSERT INTO TProfileConfig VALUES(${ProfileType.PROFILE_TYPE_RUNNING.id}, ${DistanceUnit.KILOMETERS.id}, ${SpeedUnit.FPS.id}, 1.0, 1000)")
                db.execSQL("INSERT INTO TProfileConfig VALUES(${ProfileType.PROFILE_TYPE_CYCLING.id}, ${DistanceUnit.MILES.id}, ${SpeedUnit.MPH.id}, 1.0, 1000)")
                db.execSQL("INSERT INTO TProfileConfig VALUES(${ProfileType.PROFILE_TYPE_DRIVING.id}, ${DistanceUnit.KILOMETERS.id}, ${SpeedUnit.KPH.id}, 1.0, 1000)")
                db.execSQL("INSERT INTO TProfileConfig VALUES(${ProfileType.PROFILE_TYPE_BOATING.id}, ${DistanceUnit.KILOMETERS.id}, ${SpeedUnit.KNOTS.id}, 1.0, 1000)")
                db.execSQL("INSERT INTO TProfileConfig VALUES(${ProfileType.PROFILE_TYPE_MOTORCYCLING.id}, ${DistanceUnit.MILES.id}, ${SpeedUnit.MPH.id}, 1.0, 1000)")

                //
                // DEBUG: Adding some test routes
                //

                for(n in 0..50) {
                    insertTestRoute(db)
                }
            }

            private fun insertTestRoute(db: SupportSQLiteDatabase) {
                val profileId = Random.nextInt(ProfileType.PROFILE_TYPE_WALKING.id, ProfileType.PROFILE_TYPE_MOTORCYCLING.id + 1)
                val totalDuration = TimeUnit.SECONDS.toMillis(Random.nextLong(10 * 60, 100 * 60))
                val activeDuration = totalDuration - TimeUnit.SECONDS.toMillis(Random.nextLong(10 * 60))
                val distance = Random.nextInt(500) * 1000
                val maxElevationGain = Random.nextDouble(100.0)
                val totalElevationGain = maxElevationGain + Random.nextDouble(100.0)
                val maxSpeed = Random.nextInt(1, 50)

                val endDate = Date(
                    Date().time
                            - TimeUnit.DAYS.toMillis(Random.nextLong(7))
                            - TimeUnit.HOURS.toMillis(Random.nextLong(24))
                            - TimeUnit.MINUTES.toMillis(Random.nextLong(60))
                            - TimeUnit.SECONDS.toMillis(Random.nextLong(60))
                )

                val startDate = Date(endDate.time - TimeUnit.SECONDS.toMillis(totalDuration))

                db.execSQL("""
                    INSERT INTO TRouteDetail VALUES(
                        NULL, 
                        $profileId, 
                        '${FormatUtils().formatDate(startDate, format = FormatUtils.DateFormat.FORMAT_DATABASE)}',
                        '${FormatUtils().formatDate(endDate, format = FormatUtils.DateFormat.FORMAT_DATABASE)}',
                        $totalDuration, 
                        $activeDuration,  
                        $distance, 
                        $maxElevationGain, 
                        $totalElevationGain, 
                        $maxSpeed, 
                        ${distance / TimeUnit.MILLISECONDS.toSeconds(activeDuration)} 
                    )
                    """)
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

///////////////////////////////////////////////////////////////////////////////////////////////////
// SpeedUnitTypeConverter
// Covert the SpeedUnit enum to and from an id suitable for storage in the database
///////////////////////////////////////////////////////////////////////////////////////////////////

object SpeedUnitTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toId(speedUnit: SpeedUnit): Int {
        return speedUnit.id
    }

    @TypeConverter
    @JvmStatic
    fun fromId(id: Int): SpeedUnit? {
        return SpeedUnit.fromId(id)
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// DistanceUnitTypeConverter
// Covert the DistanceUnit enum to and from an id suitable for storage in the database
///////////////////////////////////////////////////////////////////////////////////////////////////

object DistanceUnitTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toId(distanceUnit: DistanceUnit): Int {
        return distanceUnit.id
    }

    @TypeConverter
    @JvmStatic
    fun fromId(id: Int): DistanceUnit? {
        return DistanceUnit.fromId(id)
    }
}
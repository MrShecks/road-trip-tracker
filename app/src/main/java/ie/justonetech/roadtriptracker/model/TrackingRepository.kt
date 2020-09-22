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

package ie.justonetech.roadtriptracker.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import ie.justonetech.roadtriptracker.model.db.TrackingDatabase
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteDetail
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.utils.ThreadUtils

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingRepository
// Provides data from the Room database to the UI via view models
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingRepository(context: Context) {

    fun getRouteDetail(id: Int): LiveData<RouteDetail> {
        return database.routeDetailDao().getById(id)
    }

    fun getLatestRoute(): LiveData<RouteDetail> {
        return database.routeDetailDao().getLatest()
    }

    fun getRouteList(): LiveData<PagedList<RouteSummary>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_LOAD_SIZE_HINT)
            .setPageSize(PAGE_SIZE)
            .build()

        return database.routeDetailDao()
            .getList()
            .toLiveData(pagedListConfig)
    }

    fun getProfileList(): LiveData<List<ProfileConfig>> {
        return database.profileConfigDao().getList()
    }

    fun getProfile(id: Int): LiveData<ProfileConfig> {
        return database.profileConfigDao().getProfileById(id)
    }

    @Deprecated("Not needed, TrackingService will use addRoute(DbRouteDetail, List<DbRoutePoint>")
    fun addRoute(route: RouteDetail) {
        ThreadUtils().runOnDiskThread {
            with(database) {
                runInTransaction {
                    Log.d(TAG, "Inserting new route, Thread=${Thread.currentThread()}")

                    val routeId = routeDetailDao().insert(
                        DbRouteDetail(
                            null,
                            route.profileConfig.id,

                            route.startTime,
                            route.endTime,

                            route.totalDuration,
                            route.activeDuration,

                            route.distance,
                            route.maxElevationGain,
                            route.totalElevationGain,

                            route.maxSpeed,
                            route.avgSpeed
                        )
                    )

                    Log.d(TAG, "New route with id=$routeId inserted successfully")

                    if(route.points.isNotEmpty()) {
                        routePointDao().insertAll(
                            route.points.map {
                                DbRoutePoint(
                                    null,
                                    routeId.toInt(),

                                    it.timeStamp,

                                    it.latitude,
                                    it.longitude,
                                    it.altitude,

                                    it.speed,
                                    it.bearing,

                                    it.barometricAltitude
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun addRoute(route: DbRouteDetail, routePoints: List<DbRoutePoint>) {
        ThreadUtils().runOnDiskThread {
            with(database) {
                runInTransaction {
                    val routeId = routeDetailDao().insert(route)

                    if(routePoints.isNotEmpty()) {
                        routePointDao().insertAll(
                            routePoints.map {
                                DbRoutePoint(
                                    it.id,
                                    routeId.toInt(),

                                    it.timeStamp,

                                    it.latitude,
                                    it.longitude,
                                    it.altitude,

                                    it.speed,
                                    it.bearing,

                                    it.barometricAltitude
                                )
                            }
                        )
                    }

                    Log.d(TAG, "New route with id=$routeId inserted successfully")
                }
            }
        }
    }

    fun deleteRoutes(routeIds: List<Int>) {
        ThreadUtils().runOnDiskThread {
            with(database) {
                runInTransaction {
                    routeDetailDao().deleteByIds(routeIds).also {
                        Log.d(TAG, "Deleted $it routes from the database")
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val database = TrackingDatabase(context)

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingRepository::class.java.simpleName

        private const val PAGE_SIZE           = 20
        private const val PAGE_LOAD_SIZE_HINT = 20

        @Volatile
        private var instance: TrackingRepository? = null
        private var LOCK = Any()

        operator fun invoke(context: Context): TrackingRepository {
            return instance ?: synchronized(LOCK) {
                instance ?: TrackingRepository(context).also {
                    instance = it
                }
            }
        }
    }
}
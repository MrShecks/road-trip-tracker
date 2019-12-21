package ie.justonetech.roadtriptracker.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import ie.justonetech.roadtriptracker.model.db.TrackingDatabase
import ie.justonetech.roadtriptracker.model.db.dao.RouteDetailDao
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

    fun getFavouriteRouteList(): LiveData<PagedList<RouteSummary>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_LOAD_SIZE_HINT)
            .setPageSize(PAGE_SIZE)
            .build()

        return database.routeDetailDao()
            .getFavourites()
            .toLiveData(pagedListConfig)
    }

    fun getProfileList(): LiveData<List<RouteProfile>> {
        return database.routeProfileDao().getList()
    }

    fun getProfile(id: Int): LiveData<RouteProfile> {
        return database.routeProfileDao().getProfileById(id)
    }

    fun setFavouriteRoute(routeId: Int?, isFavourite: Boolean) {
        routeId?.let {
            ThreadUtils().runOnDiskThread {
                with(database) {
                    runInTransaction {
                        routeDetailDao().setFavouriteById(it, isFavourite)
                    }
                }
            }
        }
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
                            route.profile.id,

                            route.startTime,
                            route.endTime,

                            route.totalDuration,
                            route.activeDuration,

                            route.distance,
                            route.maxSpeed,
                            route.avgSpeed,
                            route.avgActiveSpeed,

                            route.isFavourite
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

    fun deleteRoute(route: RouteDetail) {
        route.id?.let { routeId ->
            deleteRoute(routeId)
        }
    }

    fun deleteRoute(routeId: Int) {
        ThreadUtils().runOnDiskThread {
            with(database) {
                runInTransaction {
                    routeDetailDao().deleteById(routeId).also {
                        Log.d(TAG, "Deleted $it routes with routeId=$routeId from database.")
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
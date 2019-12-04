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

    fun getRouteList(): LiveData<PagedList<RouteSummary>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        return database.routeDetailDao()
            .getList()
            .toLiveData(pagedListConfig)
    }

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
                            route.maxClimb
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
                                    it.accuracy
                                )
                            }
                        )
                    }
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
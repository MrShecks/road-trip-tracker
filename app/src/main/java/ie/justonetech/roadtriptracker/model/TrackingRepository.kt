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

class TrackingRepository(private val context: Context) {

    fun getRouteList(): LiveData<PagedList<RouteSummary>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        return TrackingDatabase(context)
            .routeDetailDao()
            .getList()
            .toLiveData(pagedListConfig)
    }

    fun addRoute(route: RouteDetail) {
        ThreadUtils().runOnDiskThread {
            with(TrackingDatabase(context)) {
                runInTransaction {
                    val routeId = routeDetailDao().insert(
                        DbRouteDetail(
                            route.id,

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

    fun deleteRoute(route: RouteDetail) {
        route.id?.let { routeId ->
            deleteRoute(routeId)
        }
    }

    fun deleteRoute(routeId: Int) {
        ThreadUtils().runOnDiskThread {
            with(TrackingDatabase(context)) {
                runInTransaction {
                    routeDetailDao().deleteById(routeId).also {
                        Log.d(TAG, "Deleted $it routes with routeId=$routeId from database.")
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingRepository::class.java.simpleName

        @Volatile
        private var instance: TrackingRepository? = null
        private var LOCK = Any()

        operator fun invoke(context: Context): TrackingRepository {
            return instance ?: synchronized(LOCK) {
                instance ?: TrackingRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
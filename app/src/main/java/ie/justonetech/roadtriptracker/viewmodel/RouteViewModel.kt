package ie.justonetech.roadtriptracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.model.TrackingRepository

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteViewModel
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository = TrackingRepository(application)
    private val currentRouteId = MutableLiveData<Int>()

    val routeList: LiveData<PagedList<RouteSummary>> by lazy {
        repository.getRouteList()
    }

    val favouriteRouteList: LiveData<PagedList<RouteSummary>> by lazy {
        repository.getFavouriteRouteList()
    }

    val routeDetail = Transformations.switchMap(currentRouteId) {
        repository.getRouteDetail(it)
    }

    fun fetchRouteDetail(routeId: Int) {
        currentRouteId.value = routeId
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteViewModel::class.java.simpleName
    }
}
package ie.justonetech.roadtriptracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.model.TrackingRepository

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteViewModel
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository = TrackingRepository(application)

    val routeList: LiveData<PagedList<RouteSummary>> by lazy {
        repository.getRouteList()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteViewModel::class.java.simpleName
    }
}
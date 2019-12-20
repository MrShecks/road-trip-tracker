package ie.justonetech.roadtriptracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ie.justonetech.roadtriptracker.model.RouteProfile
import ie.justonetech.roadtriptracker.model.TrackingRepository

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileViewModel
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class ProfileViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository = TrackingRepository(application)

    val profileList: LiveData<List<RouteProfile>> by lazy {
        repository.getProfileList()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteViewModel::class.java.simpleName
    }
}
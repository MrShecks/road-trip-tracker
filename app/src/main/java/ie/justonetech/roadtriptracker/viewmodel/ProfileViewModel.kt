package ie.justonetech.roadtriptracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.TrackingRepository
import ie.justonetech.roadtriptracker.utils.ProfileType

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileViewModel
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class ProfileViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository = TrackingRepository(application)
    private val currentProfile = MutableLiveData<ProfileType>()

    val profileList: LiveData<List<ProfileConfig>> by lazy {
        repository.getProfileList()
    }

    val profile = Transformations.switchMap(currentProfile) {
        repository.getProfile(it.id)
    }

    fun getProfile(profileType: ProfileType) {
        currentProfile.value = profileType
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteViewModel::class.java.simpleName
    }
}
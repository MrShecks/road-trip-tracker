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
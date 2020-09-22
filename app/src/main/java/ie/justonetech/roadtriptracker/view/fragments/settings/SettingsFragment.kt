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

package ie.justonetech.roadtriptracker.view.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.activities.MainActivity

//
// https://source.android.com/devices/tech/settings/settings-guidelines
// https://www.androidhive.info/2017/07/android-implementing-preferences-settings-screen/
// https://source.android.com/devices/tech/settings/settings-guidelines
// https://github.com/madlymad/PreferenceApp
//

///////////////////////////////////////////////////////////////////////////////////////////////////
// SettingsFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class SettingsFragment : PreferenceFragmentCompat() /* ,
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback*/ {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
    }

    // TODO: Need to figure out how to use nested preference screens with the Navigation Library
    // TODO: The Google docs seems to be lacking here...

//    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
//        Log.i(TAG, "onPreferenceStartFragment(caller=$caller, pref=$pref)")
//
//        return false
//    }
//
//    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean {
//        Log.i(TAG, "onPreferenceStartScreen(caller=$caller, pref=$pref)")
//
//        return false
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = SettingsFragment::class.java.simpleName
    }
}
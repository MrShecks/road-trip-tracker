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
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import ie.justonetech.roadtriptracker.R

//
// https://source.android.com/devices/tech/settings/settings-guidelines
// https://www.androidhive.info/2017/07/android-implementing-preferences-settings-screen/
// https://source.android.com/devices/tech/settings/settings-guidelines
// https://github.com/madlymad/PreferenceApp
//

///////////////////////////////////////////////////////////////////////////////////////////////////
// SettingsFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class GeneralPreferenceFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
    }

    override fun onPreferenceStartScreen(preferenceFragment: PreferenceFragmentCompat?, preferenceScreen: PreferenceScreen?): Boolean {
        Log.d(TAG, "onPreferenceStartScreen(): pref=$preferenceScreen")

        // FIXME: Clean up this mess
        // FIXME: Look into the correct way to do multi level preferences, I think we're supposed to just switch out the fragment

        return preferenceScreen?.let {
            when(it.key) {
                "_pref_profile_settings" -> {
                    GeneralPreferenceFragmentDirections.actionDestinationSettingsToProfileListSelection().also { action ->
                        Navigation.findNavController(requireView()).navigate(action)
                    }

                    true
                }

                else ->
                    false
            }
        } ?: false
    }

    override fun getCallbackFragment(): Fragment {
        return this
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = GeneralPreferenceFragment::class.java.simpleName
    }
}
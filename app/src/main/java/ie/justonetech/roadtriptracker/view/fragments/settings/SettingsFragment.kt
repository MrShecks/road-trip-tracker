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
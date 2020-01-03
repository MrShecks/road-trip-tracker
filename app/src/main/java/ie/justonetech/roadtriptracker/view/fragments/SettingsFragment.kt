package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import ie.justonetech.roadtriptracker.R

//
// https://source.android.com/devices/tech/settings/settings-guidelines
// https://www.androidhive.info/2017/07/android-implementing-preferences-settings-screen/
// https://source.android.com/devices/tech/settings/settings-guidelines
//

///////////////////////////////////////////////////////////////////////////////////////////////////
// SettingsFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
    }
}
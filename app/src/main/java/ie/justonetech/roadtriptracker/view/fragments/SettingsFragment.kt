package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// SettingsFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }
}
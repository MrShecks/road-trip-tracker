package ie.justonetech.roadtriptracker.view.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.adapters.ProfileListAdapter
import kotlinx.android.synthetic.main.profile_list_selection_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileListSelectionFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class ProfileListSelectionFragment : Fragment() {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_list_selection_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileList.adapter = ProfileListAdapter(view.context)
    }
}
package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.activities.TrackingActivity
import kotlinx.android.synthetic.main.home_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HomeFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTracking.setOnClickListener {
            TrackingActivity.newInstance(context!!)
        }
    }
}
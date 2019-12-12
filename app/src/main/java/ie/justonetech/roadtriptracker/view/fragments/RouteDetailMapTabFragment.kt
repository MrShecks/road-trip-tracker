package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailMapTabFragment
// Shows the map detail for a previously tracked route
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailMapTabFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_detail_map_tab_fragment, container, false)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        @JvmStatic
        fun newInstance(): RouteDetailMapTabFragment {
            return RouteDetailMapTabFragment()
        }
    }
}
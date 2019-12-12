package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailStatsTabFragment
// Shows the stats detail for a previously tracked route
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailStatsTabFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_detail_stats_tab_fragment, container, false)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        @JvmStatic
        fun newInstance(): RouteDetailStatsTabFragment {
            return RouteDetailStatsTabFragment()
        }
    }
}
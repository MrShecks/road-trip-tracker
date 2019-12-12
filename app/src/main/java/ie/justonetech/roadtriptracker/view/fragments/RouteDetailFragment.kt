package ie.justonetech.roadtriptracker.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.route_detail_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            setupRouteDetailTabs(it)
        }
    }

    private fun setupRouteDetailTabs(context: Context) {
        val adapter = ViewPagerAdapter<Fragment>(childFragmentManager).apply {

            addPage(
                context,
                R.string.route_detail_stat_tab_title,
                RouteDetailStatsTabFragment.newInstance()
            )

            addPage(
                context,
                R.string.route_detail_map_tab_title,
                RouteDetailMapTabFragment.newInstance()
            )
        }

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count

        tabLayout.setupWithViewPager(viewPager)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteDetailFragment::class.java.simpleName
    }
}
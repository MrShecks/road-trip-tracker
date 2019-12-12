package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.RouteDetailStatsTabFragmentBinding
import ie.justonetech.roadtriptracker.model.RouteDetail
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.utils.RouteDetailFormatter
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.route_detail_stats_tab_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailStatsTabFragment
// Shows the stats detail for a previously tracked route
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailStatsTabFragment : Fragment() {

    private lateinit var viewModel: RouteViewModel
    private lateinit var viewBinding: RouteDetailStatsTabFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<RouteDetailStatsTabFragmentBinding>(inflater, R.layout.route_detail_stats_tab_fragment, container, false).also {
            viewBinding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.let {
            viewModel = ViewModelProviders.of(it).get(RouteViewModel::class.java).also { model ->
                setupRouteDetailObserver(model)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupRouteDetailObserver(model: RouteViewModel) {
        model.routeDetail.observe(this, Observer<RouteDetail> { routeDetail ->
            viewBinding.routeDetail = RouteDetailFormatter(viewBinding.root.context, routeDetail)
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteDetailStatsTabFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): RouteDetailStatsTabFragment {
            return RouteDetailStatsTabFragment()
        }
    }
}
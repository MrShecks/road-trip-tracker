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

package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.RouteDetailFragmentBinding
import ie.justonetech.roadtriptracker.utils.MapUtils
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.utils.RouteDetailFormatter
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteDetailFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteDetailFragment : MapViewHostFragment() {

    private val args: RouteDetailFragmentArgs by navArgs()
    private lateinit var viewModel: RouteViewModel
    private lateinit var viewBinding: RouteDetailFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<RouteDetailFragmentBinding>(inflater, R.layout.route_detail_fragment, container, false).also {
            viewBinding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RouteViewModel::class.java).also { model ->
            setupRouteDetailObserver(model)

            model.fetchRouteDetail(args.routeId)
        }
    }

    override fun onMapReady(mapView: MapView, map: GoogleMap) {
        map.mapType = Preferences(mapView.context).mapType

        ViewModelProvider(this).get(RouteViewModel::class.java).also { model ->
            model.routeDetail.observe(viewLifecycleOwner, Observer {
                MapUtils.drawRoute(it.points, map)
            })
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupRouteDetailObserver(model: RouteViewModel) {
        model.routeDetail.observe(viewLifecycleOwner, Observer { routeDetail ->
            viewBinding.routeDetail = RouteDetailFormatter(viewBinding.root.context, routeDetail)

//            Log.i(TAG, "setupRouteDetailObserver(): Route=$routeDetail")
//            routeDetail.points.forEach {
//                Log.i(TAG, "Route Point: $it")
//            }

        })
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteDetailFragment::class.java.simpleName
    }
}
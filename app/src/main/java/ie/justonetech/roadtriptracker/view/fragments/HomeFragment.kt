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

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteDetail
import ie.justonetech.roadtriptracker.utils.FormatUtils
import ie.justonetech.roadtriptracker.utils.MapUtils
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.view.activities.MapActivity
import ie.justonetech.roadtriptracker.view.activities.TrackingActivity
import ie.justonetech.roadtriptracker.view.adapters.ProfileListAdapter
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.home_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HomeFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class HomeFragment : MapViewHostFragment() {

    private lateinit var viewModel: RouteViewModel
    private var latestRouteDetail: RouteDetail? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RouteViewModel::class.java).also { model ->
            setupLatestRouteObserver(view.context, model)
            model.fetchLatestRouteDetail()
            //model.fetchRouteDetail(2000)
        }

        profileCardView.setOnClickListener {
            val profileListAdapter = ProfileListAdapter(it.context)

            AlertDialog.Builder(it.context).apply {
                setTitle(R.string.home_fragment_profile_dialog_title)
                setAdapter(profileListAdapter) { _, which ->
                    profileListAdapter.getItem(which)?.let { selectedProfile ->
                        Preferences(context).currentProfile = selectedProfile
                        showCurrentProfile(context, selectedProfile)
                    }
                }

                show()
            }
        }

        startTracking.setOnClickListener {
            TrackingActivity.newInstance(it.context)
        }

        actionOpenFullScreenMap.setOnClickListener {
            val routeId = latestRouteDetail?.id ?: RouteDetail.INVALID_ID

            MapActivity.newInstance(it.context, routeId)
        }

        latestRouteCardView?.setOnClickListener {
            latestRouteDetail?.let { routeDetail ->
                HomeFragmentDirections.actionDestinationHomeToRouteDetail(routeDetail.id!!).also { action ->
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }

        showCurrentProfile(view.context, Preferences(view.context).currentProfile)
    }

    private fun showCurrentProfile(context: Context, profile: ProfileType) {
        profileName.setText(profile.nameId)
        profileName.setCompoundDrawablesWithIntrinsicBounds(
            AppCompatResources.getDrawable(context, profile.drawableId),
            null,
            AppCompatResources.getDrawable(context, R.drawable.ic_arrow_drop_down_black_24dp),
            null
        )

        profileTag.setBackgroundColor(profile.getColor(context))
    }

    override fun onMapReady(mapView: MapView, map: GoogleMap) {

        map.mapType = Preferences(mapView.context).mapType

        ViewModelProvider(this).get(RouteViewModel::class.java).also { model ->
            model.routeDetail.observe(viewLifecycleOwner, Observer {
                if(it != null)
                    MapUtils.drawRoute(it.points, map)
            })
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupLatestRouteObserver(context: Context, model: RouteViewModel) {

        model.routeDetail.observe(viewLifecycleOwner, Observer { routeDetail ->
            if(routeDetail != null) {
                latestRouteProfileName?.setText(routeDetail.profileType.nameId)
                latestRouteProfileName?.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, routeDetail.profileType.drawableId),
                    null,
                    null,
                    null
                )

                latestRouteStartTime?.text = FormatUtils().formatDate(routeDetail.startTime, format = FormatUtils.DateFormat.FORMAT_SHORT_SHORT_TIME)
                latestRouteProfileTag?.setBackgroundColor(ContextCompat.getColor(context,routeDetail.profileType.colorId))

            } else {
                latestRouteProfileName?.text = getString(R.string.home_fragment_no_route_available_message)
                latestRouteProfileName?.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, R.drawable.ic_info_outline_white_24dp),
                    null,
                    null,
                    null
                )

                latestRouteStartTime?.visibility = View.GONE
            }

            latestRouteDetail = routeDetail
        })
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }
}
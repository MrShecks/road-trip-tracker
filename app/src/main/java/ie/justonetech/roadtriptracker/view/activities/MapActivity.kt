package ie.justonetech.roadtriptracker.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteDetail
import ie.justonetech.roadtriptracker.utils.MapUtils
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.map_activity.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// MapActivity
// Activity used to display a full screen route map or map of current location
///////////////////////////////////////////////////////////////////////////////////////////////////

class MapActivity
    : AppCompatActivity(), OnMapReadyCallback {

    private var routeId: Int = RouteDetail.INVALID_ID
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.map_activity)
        mapView.onCreate(savedInstanceState)

        intent?.let {
            routeId = it.getIntExtra(ARG_ROUTE_ID, RouteDetail.INVALID_ID)
            currentLocation = it.getSerializableExtra(ARG_CURRENT_LOCATION) as LatLng?
        }

        mapView.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onMapReady(map: GoogleMap) {
        map.mapType = Preferences(this).mapType

        if(routeId != RouteDetail.INVALID_ID) {
            ViewModelProvider(this).get(RouteViewModel::class.java).also { model ->
                model.routeDetail.observe(this, Observer {
                    MapUtils.drawRoute(it.points, map, DEFAULT_MAP_ZOOM)
                })

                model.fetchRouteDetail(routeId)
            }

        } else if(currentLocation != null) {
            MapUtils.drawLocation(currentLocation!!, map, DEFAULT_MAP_ZOOM)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MapActivity::class.java.simpleName

        private const val DEFAULT_MAP_ZOOM: Float   = 13.5f

        private const val ARG_ROUTE_ID = "_route_id"
        private const val ARG_CURRENT_LOCATION = "_current_location"

        @JvmStatic
        fun newInstance(context: Context, routeId: Int) {
            val intent = Intent(context, MapActivity::class.java)

            intent.putExtra(ARG_ROUTE_ID, routeId)
            context.startActivity(intent)
        }

        @JvmStatic
        fun newInstance(context: Context, currentLocation: LatLng) {
            val intent = Intent(context, MapActivity::class.java)

            intent.putExtra(ARG_CURRENT_LOCATION, currentLocation)
            context.startActivity(intent)
        }
    }
}
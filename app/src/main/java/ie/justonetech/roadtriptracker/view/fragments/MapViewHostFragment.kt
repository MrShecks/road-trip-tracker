package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback



///////////////////////////////////////////////////////////////////////////////////////////////////
// MapViewHostFragment
// Utility base class for fragments that host a MapView
///////////////////////////////////////////////////////////////////////////////////////////////////

open class MapViewHostFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    fun onViewCreated(view: View, savedInstanceState: Bundle?, mapView: MapView) {
        super.onViewCreated(view, savedInstanceState)
        this.mapView = mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapViewHostFragment)
        }
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
        mapView .onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMapReady(map: GoogleMap?) {

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MapViewHostFragment::class.java.simpleName
    }
}
package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

///////////////////////////////////////////////////////////////////////////////////////////////////
// MapViewHostFragment
// Utility base class for fragments that host a MapView
///////////////////////////////////////////////////////////////////////////////////////////////////

open class MapViewHostFragment : Fragment() {

    private var childMapViews = emptyList<MapView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findChildMapViews(view).also {
            it.forEach { mapView ->
                mapView.onCreate(savedInstanceState)
                mapView.getMapAsync { map ->
                    onMapReady(mapView, map)
                }
            }

            childMapViews = it
        }

        Log.i(TAG, "Maps Found: $childMapViews")
    }

    override fun onStart() {
        super.onStart()
        childMapViews.forEach { it.onStart() }
    }

    override fun onPause() {
        super.onPause()
        childMapViews.forEach { it.onPause() }
    }

    override fun onResume() {
        super.onResume()
        childMapViews.forEach { it.onResume() }
    }

    override fun onStop() {
        super.onStop()
        childMapViews.forEach { it.onStop() }
    }

    override fun onDestroy() {
        // FIXME: For MapBox this needed to be done in onDestroyView(), need to check if this is the case with Google Maps

        childMapViews.forEach { it.onDestroy() }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        childMapViews.forEach { it.onSaveInstanceState(outState) }
    }

    open fun onMapReady(mapView: MapView, map: GoogleMap) {
        // TO be overridden by sub classes
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun findChildMapViews(parent: View): List<MapView> {
        val maps = mutableListOf<MapView>()

        if(parent is ViewGroup) {
            parent.children.forEach { childView ->

                //
                // Note: MapView inherits from FrameLayout so it is a ViewGroup but
                // it is not recommend to add children to MapViews (especially not
                // child MapViews) so we don't recurse into its children.
                //

                if(childView is MapView)
                    maps.add(childView)
                else if(childView is ViewGroup)
                    maps.addAll(findChildMapViews(childView))
            }
        }

        return maps
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MapViewHostFragment::class.java.simpleName
    }
}
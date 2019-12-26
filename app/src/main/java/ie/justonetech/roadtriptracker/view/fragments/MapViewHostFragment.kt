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
// Utility base class for fragments that host a MapView, automatically handles forwarding Fragment
// lifecycle calls to all child MapViews contained in the Fragment
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

    override fun onDestroyView() {

        //
        // Note: For fragments we call MapView.onDestroy() in onDestroyView()
        //

        childMapViews.forEach { it.onDestroy() }
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        childMapViews.forEach { it.onLowMemory() }
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
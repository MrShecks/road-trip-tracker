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

    override fun onLowMemory() {
        super.onLowMemory()
        childMapViews.forEach { it.onLowMemory() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        childMapViews.forEach { it.onSaveInstanceState(outState) }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        childMapViews.forEach { it.onDestroy() }
        super.onDestroy()
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
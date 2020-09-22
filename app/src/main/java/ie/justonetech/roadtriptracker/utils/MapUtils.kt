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

package ie.justonetech.roadtriptracker.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import ie.justonetech.roadtriptracker.model.RouteDetail

///////////////////////////////////////////////////////////////////////////////////////////////////
// MapUtils
///////////////////////////////////////////////////////////////////////////////////////////////////

object MapUtils {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    fun drawRoute(routePoints: List<RouteDetail.Point>, map: GoogleMap, mapZoom: Float = DEFAULT_MAP_ZOOM) {
        if(routePoints.isNotEmpty()) {
            val boundingRect = getBoundingRect(routePoints)

            map.clear()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(boundingRect.center, mapZoom))

            map.addMarker(MarkerOptions().apply {
                title("Start")
                position(routePoints.first().latLng)
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            })

            map.addPolyline(PolylineOptions().apply {
                routePoints.forEach {
                    add(it.latLng)
                }
            })

            if (routePoints.size > 1) {
                map.addMarker(MarkerOptions().apply {
                    title("End")
                    position(routePoints.last().latLng)
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                })
            }

            map.addPolygon(getBoundingPolygon(boundingRect))
        }
    }

    fun drawLocation(location: LatLng, map: GoogleMap, mapZoom: Float = DEFAULT_MAP_ZOOM) {
        // TODO: Set marker at location and move camera

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getBoundingRect(routePoints: List<RouteDetail.Point>): LatLngBounds {
        return LatLngBounds.Builder().apply {
            routePoints.forEach {
                include(it.latLng)
            }

        }.build()
    }

    private fun getBoundingPolygon(boundingRect: LatLngBounds, @ColorInt fillColor: Int = Color.BLUE.withAlpha(15)): PolygonOptions {
        return PolygonOptions().apply {
            add(boundingRect.northeast)
            add(boundingRect.southeast)
            add(boundingRect.southwest)
            add(boundingRect.northwest)

            strokeColor(Color.TRANSPARENT)
            fillColor(fillColor)
        }
    }

    @ColorInt
    private fun Int.withAlpha(alpha: Int): Int {
        require(alpha in 0..0xFF)

        return this and 0x00FFFFFF or (alpha shl 24)
    }

    private val LatLngBounds.southeast: LatLng
        get() = LatLng(northeast.latitude, southwest.longitude)

    private val LatLngBounds.northwest: LatLng
        get() = LatLng(southwest.latitude, northeast.longitude)

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private const val DEFAULT_MAP_ZOOM: Float   = 12f
}
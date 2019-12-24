package ie.justonetech.roadtriptracker.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import ie.justonetech.roadtriptracker.model.RouteDetail

///////////////////////////////////////////////////////////////////////////////////////////////////
// MapUtils
///////////////////////////////////////////////////////////////////////////////////////////////////

object MapUtils {
    fun drawRoute(routePoints: List<RouteDetail.Point>, map: GoogleMap) {
        if(routePoints.isNotEmpty()) {
            map.clear()

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
        }
    }
}
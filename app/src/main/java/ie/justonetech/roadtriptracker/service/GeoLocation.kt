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

package ie.justonetech.roadtriptracker.service

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.absoluteValue

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// GeoLocation
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class GeoLocation(location: Location, private val barometricAltitude: Double) : Location(location) {

    val latLng by lazy {
        LatLng(latitude, longitude)
    }

    fun getGpsAltitude(): Double {
        return super.getAltitude()
    }

    fun getBarometricAltitude(): Double {
        return barometricAltitude.absoluteValue
    }

    override fun getAltitude(): Double {

        //
        // Prefer Barometric Altitude over GPS altitude if available
        //

        return if(barometricAltitude == 0.0)
            super.getAltitude()
        else {

            //
            // Note: SensorManager.getAltitude() can return a negative value if the current air pressure
            // is less than PRESSURE_STANDARD_ATMOSPHERE so we take the absolute value. This should be
            // ok since we are only using the barometric altitude to calculated relative elevation changes.
            //

            barometricAltitude.absoluteValue
        }
    }
}
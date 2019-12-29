package ie.justonetech.roadtriptracker.service

import android.location.Location
import kotlin.math.absoluteValue

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// LocationFix
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class LocationFix(location: Location, private val barometricAltitude: Double) : Location(location) {

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
package ie.justonetech.roadtriptracker.utils

import android.content.Context
import androidx.annotation.StringRes
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// DistanceUnit
// Supported distance units and conversion utilities
///////////////////////////////////////////////////////////////////////////////////////////////////

enum class DistanceUnit(val id: Int, @StringRes private val suffixId: Int) {

    // Note: The enum 'id' value is stored in application database
    // so care should be taken to keep the ids consistent when
    // adding or removing enums

    METERS(3001, R.string.distance_unit_suffix_meters) {
        override fun toMeters(d: Double)        = d
        override fun toMiles(d: Double)         = d * MI_PER_METER
        override fun toKilometers(d: Double)    = d / 1000
    },

    MILES(3002, R.string.distance_unit_suffix_miles) {
        override fun toMeters(d: Double)        = d / MI_PER_METER
        override fun toMiles(d: Double)         = d
        override fun toKilometers(d: Double)    = d / MI_PER_KM
    },

    KILOMETERS(3003, R.string.distance_unit_suffix_kilometers) {
        override fun toMeters(d: Double)        = d * 1000
        override fun toMiles(d: Double)         = d * MI_PER_KM
        override fun toKilometers(d: Double)    = d
    };

    abstract fun toMeters(d: Double): Double
    abstract fun toMiles(d: Double): Double
    abstract fun toKilometers(d: Double): Double

    fun convertTo(d: Double, targetUnit: DistanceUnit) = when(targetUnit) {
        METERS      -> toMeters(d)
        MILES       -> toMiles(d)
        KILOMETERS  -> toKilometers(d)
    }

    fun getSuffix(context: Context): String = context.getString(suffixId)

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private const val MI_PER_METER: Double  = 0.0006213712
        private const val MI_PER_KM: Double     = 0.62137

        @JvmStatic
        fun fromId(id: Int, defaultValue: DistanceUnit = KILOMETERS): DistanceUnit {
            return values().firstOrNull { it.id == id } ?: defaultValue
        }
    }
}
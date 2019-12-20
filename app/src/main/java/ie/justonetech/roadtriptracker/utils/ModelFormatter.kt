package ie.justonetech.roadtriptracker.utils

import android.content.Context
import java.util.concurrent.TimeUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// ModelFormatter
// Base class containing common utility function for formatting model data
///////////////////////////////////////////////////////////////////////////////////////////////////

open class ModelFormatter<T>(protected val context: Context, val data: T) {

    protected fun formatDuration(duration: Long, sourceUnits: TimeUnit = TimeUnit.MILLISECONDS): String {
        return FormatUtils().formatDuration(duration, sourceUnits)
    }

    protected fun formatDistance(distance: Double, targetUnit: DistanceUnit, withSuffix: Boolean = true): String {
        return FormatUtils().formatDistance(context, distance, DistanceUnit.METERS, targetUnit, withSuffix)
    }

    protected fun formatSpeed(speed: Float, targetUnit: SpeedUnit, withSuffix: Boolean = true): String {
        return FormatUtils().formatSpeed(context, speed, SpeedUnit.MPS, targetUnit, withSuffix)
    }
}
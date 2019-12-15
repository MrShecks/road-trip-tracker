package ie.justonetech.roadtriptracker.utils

import android.content.Context
import java.util.concurrent.TimeUnit

///////////////////////////////////////////////////////////////////////////////////////////////////
// ModelFormatter
// Base class containing common utility function for formatting model data
///////////////////////////////////////////////////////////////////////////////////////////////////

open class ModelFormatter<T>(protected val context: Context, val data: T) {

    protected fun formatDuration(duration: Long) = FormatUtils().formatDuration(duration, TimeUnit.SECONDS)

    protected fun formatDistance(distance: Double, targetUnit: DistanceUnit, withSuffix: Boolean = true): String {
        return FormatUtils().formatDistance(context, distance, DistanceUnit.KILOMETERS, targetUnit, withSuffix)
    }

    protected fun formatSpeed(speed: Float, targetUnit: SpeedUnit, withSuffix: Boolean = true): String {
        return FormatUtils().formatSpeed(context, speed, SpeedUnit.KPH, targetUnit, withSuffix)
    }
}
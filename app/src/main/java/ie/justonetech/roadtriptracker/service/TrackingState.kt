package ie.justonetech.roadtriptracker.service

import android.location.Location
import android.util.Log
import ie.justonetech.roadtriptracker.utils.ElapsedTimer
import java.util.*
import java.util.concurrent.TimeUnit

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingState
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingState {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LocationPoint
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    class LocationPointEx(location: Location, private val barometricAltitude: Float) : Location(location) {
        fun getBarometricAltitude(): Float = barometricAltitude
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LiveStats
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    data class LiveStats(
        val startTimestamp: Date,
        val endTimestamp: Date,
        val totalDuration: Long,
        val activeDuration: Long,

        val maxSpeed: Float,
        val avgSpeed: Float
    )

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    var profileId: Int = 0
        private set

    val totalDuration = ElapsedTimer()
    val activeDuration = ElapsedTimer()

    var distance: Double = 0.0
        private set

    var currentSpeed: Float = 0.0f
        private set

    var maxSpeed: Float = 0.0f
        private set

    var avgSpeed: Float = 0.0f
        private set

    var avgActiveSpeed: Float = 0.0f
        private set

    var maxElevationGain: Float = 0.0f
        private set

    var totalElevationGain: Float = 0.0f
        private set

    var locationPoints = mutableListOf<LocationPointEx>()
        private set

    private var sampleInterval: Float = 0f

    private var currentElevationGain: Float = 0f
    private var previousAltitude: Float = Float.NaN

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun start(profileId: Int, sampleInterval: Float) {

        reset()

        totalDuration.start()
        activeDuration.start()

        this.profileId = profileId
        this.sampleInterval = sampleInterval
    }

    fun stop() {
        totalDuration.stop()
        activeDuration.stop()
    }

    fun pause() {
        activeDuration.pause()
    }

    fun resume() {
        activeDuration.resume()
    }

    fun update(location: Location, barometricAltitude: Float): Boolean {
        val distanceMoved = if(locationPoints.isNotEmpty()) locationPoints.last().distanceTo(location) else 0.0f
        var result = false

        if(locationPoints.isEmpty() || distanceMoved > sampleInterval) {
            LocationPointEx(location, barometricAltitude).also {
                distance += distanceMoved
                currentSpeed = it.speed

                if(currentSpeed > maxSpeed)
                    maxSpeed = it.speed

                if(totalDuration.getElapsedTime() > 0)
                    avgSpeed = (distance / totalDuration.getElapsedTime(TimeUnit.SECONDS)).toFloat()

                if(activeDuration.getElapsedTime() > 0)
                    avgActiveSpeed = (distance / activeDuration.getElapsedTime(TimeUnit.SECONDS)).toFloat()

                if(!previousAltitude.isNaN()) {
                    if(barometricAltitude > previousAltitude) {
                        currentElevationGain += barometricAltitude - previousAltitude
                        totalElevationGain += barometricAltitude - previousAltitude
                    }
                    else
                        currentElevationGain = 0f

                    if(currentElevationGain > maxElevationGain)
                        maxElevationGain = currentElevationGain
                }

                previousAltitude = barometricAltitude
                locationPoints.add(it)

                Log.i(TAG, "BarometricAltitude=$barometricAltitude, previousAltitude=$previousAltitude, maxElevationGain=$maxElevationGain, totalElevationGain=$totalElevationGain, currentElevationGain=$currentElevationGain")
            }

            result = true
        }

        return result
    }

    private fun reset() {
        distance = 0.0

        currentSpeed = 0.0f
        maxSpeed = 0.0f
        avgSpeed = 0.0f
        avgActiveSpeed = 0.0f

        maxElevationGain = 0.0f
        totalElevationGain = 0.0f
        currentElevationGain = 0f

        previousAltitude = Float.NaN

        locationPoints.clear()
    }

    override fun toString(): String {
        return "Started=${totalDuration.startTime}, " +
                "Stopped=${totalDuration.endTime}, " +
                "totalDuration=${totalDuration.getElapsedTime()}, " +
                "activeDuration=${activeDuration.getElapsedTime()}"
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingState::class.java.simpleName
    }
}
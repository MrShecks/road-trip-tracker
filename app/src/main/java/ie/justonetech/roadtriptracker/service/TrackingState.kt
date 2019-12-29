package ie.justonetech.roadtriptracker.service

import android.util.Log
import ie.justonetech.roadtriptracker.utils.ElapsedTimer
import java.util.*
import java.util.concurrent.TimeUnit

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingState
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingState {

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

    var maxElevationGain: Double = 0.0
        private set

    var totalElevationGain: Double = 0.0
        private set

    var locationPoints = mutableListOf<LocationFix>()
        private set

    private var sampleInterval: Float = 0f

    private var currentElevationGain: Double = 0.0
    private var previousAltitude: Double = Double.NaN

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

    fun update(location: LocationFix): Boolean {
        val distanceMoved = if(locationPoints.isNotEmpty()) locationPoints.last().distanceTo(location) else 0.0f
        var result = false

        if(locationPoints.isEmpty() || distanceMoved > sampleInterval) {
            distance += distanceMoved
            currentSpeed = location.speed

            if(currentSpeed > maxSpeed)
                maxSpeed = location.speed

            if(totalDuration.getElapsedTime() > 0)
                avgSpeed = (distance / totalDuration.getElapsedTime(TimeUnit.SECONDS)).toFloat()

            if(activeDuration.getElapsedTime() > 0)
                avgActiveSpeed = (distance / activeDuration.getElapsedTime(TimeUnit.SECONDS)).toFloat()

            if(!previousAltitude.isNaN()) {
                if(location.altitude > previousAltitude) {
                    val altitudeGain = location.altitude - previousAltitude

                    currentElevationGain += altitudeGain
                    totalElevationGain += altitudeGain
                }
                else
                    currentElevationGain = 0.0

                if(currentElevationGain > maxElevationGain)
                    maxElevationGain = currentElevationGain
            }

            previousAltitude = location.altitude
            locationPoints.add(location)

            Log.i(TAG, "BarometricAltitude=${location.altitude}, previousAltitude=$previousAltitude, maxElevationGain=$maxElevationGain, totalElevationGain=$totalElevationGain, currentElevationGain=$currentElevationGain")

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

        maxElevationGain = 0.0
        totalElevationGain = 0.0
        currentElevationGain = 0.0

        previousAltitude = Double.NaN

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
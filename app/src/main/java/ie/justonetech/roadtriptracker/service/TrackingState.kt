package ie.justonetech.roadtriptracker.service

import android.location.Location
import ie.justonetech.roadtriptracker.utils.ElapsedTimer
import kotlin.collections.ArrayList

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingState
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingState {

    var distance: Double = 0.0
        private set

    var maxSpeed: Float = 0.0f
        private set

    var totalSpeed: Float = 0.0f
        private set

    var currentSpeed: Float = 0.0f
        private set

    var currentPace: Float = 0.0f
        private set

    var minAltitude: Double = Double.MAX_VALUE
        private set

    var maxAltitude: Double = Double.MIN_VALUE
        private set

    var totalDuration = ElapsedTimer()
        private set

    var activeDuration = ElapsedTimer()
        private set

    var locationPoints = ArrayList<Location>()
        private set

    val maxClimb: Double
        get() = 0.0             // FIXME: Calculate from location points and altitude

    val avgSpeed: Float
        get() = 0.0f            // FIXME: Calculate from location points

    val startTimestamp get() = totalDuration.startTimestamp
    val endTimestamp get() = totalDuration.endTimestamp

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun start() {
        reset()

        totalDuration.start()
        activeDuration.start()
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

    fun update(locations: List<Location>, minChangeInMeters: Int = 0): Boolean {
        return false
    }

    fun reset() {
        distance = 0.0
        maxSpeed = 0.0f
        totalSpeed = 0.0f
        currentSpeed = 0.0f
        currentPace = 0.0f

        minAltitude = Double.MAX_VALUE
        maxAltitude = Double.MIN_VALUE

        locationPoints.clear()
        totalDuration = ElapsedTimer()
        activeDuration = ElapsedTimer()
    }

    override fun toString(): String {
        return "Started=$startTimestamp, " +
                "Stopped=$endTimestamp, " +
                "totalDuration=${totalDuration.getElapsedTime()}, " +
                "activeDuration=${activeDuration.getElapsedTime()}"
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingState::class.java.simpleName
    }
}
package ie.justonetech.roadtriptracker.service

import android.location.Location
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

    var locationPoints = mutableListOf<LocationPointEx>()
        private set

    private var gpsResolution: Int = 0


    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun start(profileId: Int, gpsResolution: Int) {

        totalDuration.start()
        activeDuration.start()

        this.profileId = profileId
        this.gpsResolution = gpsResolution
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

        if(locationPoints.isEmpty() || distanceMoved > gpsResolution) {
            LocationPointEx(location, barometricAltitude).also {
                distance += distanceMoved
                currentSpeed = it.speed

                if(currentSpeed > maxSpeed)
                    maxSpeed = it.speed

                if(totalDuration.getElapsedTime() > 0)
                    avgSpeed = (distance / totalDuration.getElapsedTime(TimeUnit.SECONDS)).toFloat()

                if(activeDuration.getElapsedTime() > 0)
                    avgActiveSpeed = (distance / activeDuration.getElapsedTime(TimeUnit.SECONDS)).toFloat()

                locationPoints.add(it)
            }

            result = true
        }

        return result
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
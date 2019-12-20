package ie.justonetech.roadtriptracker.utils

import java.util.*
import java.util.concurrent.TimeUnit

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// ElapsedTimer
// Helper class for tracking elapsed time intervals
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class ElapsedTimer {

    enum class State {
        STARTED,
        STOPPED,
        PAUSED
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    var state = State.STOPPED
        private set

    val startTime: Date
        get() = Date(startTimeInMillis)

    val endTime: Date
        get() = Date(endTimeInMillis)

    private var startTimeInMillis: Long = 0
    private var endTimeInMillis: Long = 0

    fun start() {
        check(state == State.STOPPED) { "Elapsed timer can only be started from the stopped state (state=$state)" }

        startTimeInMillis = System.currentTimeMillis()
        endTimeInMillis = 0
        state = State.STARTED
    }

    fun stop() {
        check(state == State.STARTED || state == State.PAUSED) { "Elapsed timer can only be stopped from the started or paused state (state=$state)" }

        endTimeInMillis = System.currentTimeMillis()
        state = State.STOPPED
    }

    fun pause() {
        check(state == State.STARTED) { "Elapsed timer can only be paused from the started state (state=$state)" }

        endTimeInMillis = System.currentTimeMillis()
        state = State.PAUSED
    }

    fun resume() {
        check(state == State.PAUSED) { "Elapsed timer can only be resumed from the paused state (state=$state)" }

        startTimeInMillis += System.currentTimeMillis() - endTimeInMillis
        state = State.STARTED
    }

    fun getElapsedTime(targetUnit: TimeUnit = TimeUnit.MILLISECONDS): Long = when(state) {
        State.STOPPED, State.PAUSED -> targetUnit.convert(endTimeInMillis - startTimeInMillis, TimeUnit.MILLISECONDS)
        State.STARTED               -> targetUnit.convert(System.currentTimeMillis() - startTimeInMillis, TimeUnit.MILLISECONDS)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = ElapsedTimer::class.java.simpleName
    }
}
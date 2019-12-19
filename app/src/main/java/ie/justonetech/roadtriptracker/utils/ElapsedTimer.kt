package ie.justonetech.roadtriptracker.utils

import java.util.*

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

    val startTimestamp: Date
        get() = Date(startTime)

    val endTimestamp: Date
        get() = Date(endTime)

    private var startTime: Long = 0
    private var endTime: Long = 0

    fun start() {
        check(state == State.STOPPED) { "Elapsed timer can only be started from the stopped state (state=$state)" }

        startTime = System.currentTimeMillis()
        endTime = 0
        state = State.STARTED
    }

    fun stop() {
        check(state == State.STARTED || state == State.PAUSED) { "Elapsed timer can only be stopped from the started or paused state (state=$state)" }

        endTime = System.currentTimeMillis()
        state = State.STOPPED
    }

    fun pause() {
        check(state == State.STARTED) { "Elapsed timer can only be paused from the started state (state=$state)" }

        endTime = System.currentTimeMillis()
        state = State.PAUSED
    }

    fun resume() {
        check(state == State.PAUSED) { "Elapsed timer can only be resumed from the paused state (state=$state)" }

        startTime += System.currentTimeMillis() - endTime
        state = State.STARTED
    }

    fun getElapsedTime(): Long = when(state) {
        State.STOPPED, State.PAUSED -> endTime - startTime
        State.STARTED               -> System.currentTimeMillis() - startTime
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = ElapsedTimer::class.java.simpleName
    }
}
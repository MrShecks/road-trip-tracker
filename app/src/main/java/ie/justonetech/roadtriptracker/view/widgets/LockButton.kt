package ie.justonetech.roadtriptracker.view.widgets

import android.content.Context
import android.os.CountDownTimer
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageButton
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// LockButton
// Custom button that triggers once a touch event has been maintained for a set time
///////////////////////////////////////////////////////////////////////////////////////////////////

class LockButton : AppCompatImageButton {

    interface LockStateChangedListener {
        fun onBeginLock(button: LockButton)
        fun onEndLock(button: LockButton)
        fun onCancelLock(button: LockButton)
        fun onLockProgress(button: LockButton, progress: Int)
        fun onLockStateChanged(buttons: LockButton, isLocked: Boolean)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    var isLocked = false
        private set

    var lockDelay = DEFAULT_LOCK_DELAY
    var lockProgressInterval = DEFAULT_LOCK_PROGRESS_INTERVAL

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private var lockStateChangedListener : LockStateChangedListener? = null
    private var touchTimer: CountDownTimer? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {

        isSaveEnabled = true
        setImageResourceFromState(isLocked)

        setOnTouchListener{ view: View, event: MotionEvent ->
            val lockButton = view as LockButton

            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchTimer = object: CountDownTimer(lockDelay, lockProgressInterval) {

                        override fun onTick(millisUntilFinished: Long) {
                            lockStateChangedListener?.let {
                                handler.post {
                                    val percentComplete = ((lockDelay - millisUntilFinished) / lockDelay.toFloat()) * 100f

                                    it.onLockProgress(lockButton, percentComplete.toInt())
                                }
                            }
                        }

                        override fun onFinish() {
                            isLocked = !isLocked

                            handler.post {
                                setImageResourceFromState(isLocked)
                                performClick()

                                lockStateChangedListener?.let {
                                    it.onLockProgress(lockButton, 100)
                                    it.onLockStateChanged(lockButton, isLocked)
                                    it.onEndLock(lockButton)
                                }
                            }
                        }
                    }

                    lockStateChangedListener?.let {
                        handler.post { it.onBeginLock(lockButton) }
                    }

                    touchTimer?.start()

                    true
                }

                MotionEvent.ACTION_UP -> {
                    touchTimer?.cancel()
                    touchTimer = null

                    lockStateChangedListener?.let {
                        handler.post { it.onCancelLock(lockButton) }
                    }

                    true
                }

                else -> false
            }
        }
    }

    @MainThread
    fun setLocked(isLocked: Boolean, notifyLockListener: Boolean = true) : Boolean {
        if(this.isLocked != isLocked) {
            setImageResourceFromState(isLocked)
            this.isLocked = isLocked

            if(notifyLockListener)
                lockStateChangedListener?.onLockStateChanged(this, this.isLocked)
        }

        return this.isLocked != isLocked
    }

    fun setOnLockStateChangedListener(lockStateChangedListener: LockStateChangedListener) {
        this.lockStateChangedListener = lockStateChangedListener
    }

    override fun onSaveInstanceState(): Parcelable? {
        return LockButtonSavedState(super.onSaveInstanceState()).apply {
            isLocked = this@LockButton.isLocked
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        with(state as LockButtonSavedState) {
            super.onRestoreInstanceState(state)

            this@LockButton.isLocked = isLocked
            setImageResourceFromState(isLocked)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setImageResourceFromState(isLocked: Boolean) {
        setImageResource(
            if(isLocked)
                R.drawable.ic_lock_closed_white_24dp
            else
                R.drawable.ic_lock_open_white_24dp
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // SavedState
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private class LockButtonSavedState : BaseSavedState {
        var isLocked: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            isLocked = parcel.readInt() != 0
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)

            out?.writeInt(if(isLocked) 1 else 0)
        }

        @JvmField
        val CREATOR: Parcelable.Creator<LockButtonSavedState> = object : Parcelable.Creator<LockButtonSavedState> {
            override fun createFromParcel(source: Parcel): LockButtonSavedState {
                return LockButtonSavedState(source)
            }

            override fun newArray(size: Int): Array<LockButtonSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = LockButton::class.java.simpleName

        private const val DEFAULT_LOCK_DELAY : Long                 = 1500
        private const val DEFAULT_LOCK_PROGRESS_INTERVAL : Long     = 10
    }

}
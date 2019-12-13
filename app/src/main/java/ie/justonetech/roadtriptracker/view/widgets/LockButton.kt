package ie.justonetech.roadtriptracker.view.widgets

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    var locked = false
        private set

    var lockDelay = DEFAULT_LOCK_DELAY

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private var lockStateChangedListener : LockStateChangedListener? = null
    private var touchTimer: CountDownTimer? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {

        setImageResource(R.drawable.ic_lock_open_white_24dp)

        setOnTouchListener{ view: View, event: MotionEvent ->
            val lockButton = view as LockButton

            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchTimer = object: CountDownTimer(lockDelay, DEFAULT_LOCK_PROGRESS_INTERVAL) {

                        override fun onTick(millisUntilFinished: Long) {
                            lockStateChangedListener?.let {
                                handler.post {
                                    val percentComplete = ((lockDelay - millisUntilFinished) / lockDelay.toFloat()) * 100f

                                    it.onLockProgress(lockButton, percentComplete.toInt())
                                }
                            }
                        }

                        override fun onFinish() {
                            val imageId = if(locked) R.drawable.ic_lock_open_white_24dp else R.drawable.ic_lock_closed_white_24dp

                            locked = !locked

                            handler.post {
                                setImageResource(imageId)
                                performClick()

                                lockStateChangedListener?.let {
                                    it.onLockProgress(lockButton, 100)
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
                    Log.d(TAG, "LockButton: Touch, ACTION_UP")

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

    fun setOnLockStateChangedListener(lockStateChangedListener: LockStateChangedListener) {
        this.lockStateChangedListener = lockStateChangedListener
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = LockButton::class.java.simpleName

        private const val DEFAULT_LOCK_DELAY : Long                 = 1500
        private const val DEFAULT_LOCK_PROGRESS_INTERVAL : Long     = 10
    }

}
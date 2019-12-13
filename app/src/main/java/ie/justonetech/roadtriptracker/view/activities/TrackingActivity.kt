package ie.justonetech.roadtriptracker.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.widgets.ImageToast
import ie.justonetech.roadtriptracker.view.widgets.LockButton
import kotlinx.android.synthetic.main.tracking_activity.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingActivity
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracking_activity)

        lockButton.setOnLockStateChangedListener(object: LockButton.LockStateChangedListener{
            override fun onBeginLock(button: LockButton) {
                lockProgress.progress = 0
                lockProgress.visibility = View.VISIBLE
            }

            override fun onEndLock(button: LockButton) {
                val isLocked = button.locked

                startStopButton.isEnabled = !isLocked
                pauseResumeButton.isEnabled = !isLocked
                lockProgress.visibility = View.INVISIBLE

                showLockMessage(this@TrackingActivity, isLocked)
            }

            override fun onCancelLock(button: LockButton) {
                lockProgress.visibility = View.INVISIBLE
            }

            override fun onLockProgress(button: LockButton, progress: Int) {
                lockProgress.progress = progress
            }
        })
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showLockMessage(context: Context, isLocked: Boolean) {
        val stringId = if(isLocked) R.string.tracking_screen_locked else R.string.tracking_screen_unlocked

        showLockMessage(context, stringId, isLocked)
    }

    private fun showLockMessage(context: Context, @StringRes stringId: Int, isLocked: Boolean) {
        showLockMessage(context, context.getString(stringId), isLocked)
    }

    private fun showLockMessage(context: Context, message: String, isLocked: Boolean) {
        val imageId = if(isLocked) R.drawable.ic_lock_closed_white_24dp else R.drawable.ic_lock_open_white_24dp

        with(ImageToast.create(context, imageId, message, Toast.LENGTH_SHORT)) {
            setGravity(android.view.Gravity.CENTER, 0, 0)
            show()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingActivity::class.java.simpleName

        @JvmStatic
        fun newInstance(context: Context) {
            val intent = Intent(context, TrackingActivity::class.java)

            context.startActivity(intent)
        }
    }
}
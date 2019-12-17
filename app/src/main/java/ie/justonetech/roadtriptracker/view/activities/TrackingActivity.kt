package ie.justonetech.roadtriptracker.view.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.service.TrackingService
import ie.justonetech.roadtriptracker.view.widgets.ImageToast
import ie.justonetech.roadtriptracker.view.widgets.LockButton
import kotlinx.android.synthetic.main.tracking_activity.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingActivity
// Active route tracking active. Starts/stops route tracking service and displays live stats
// and route progress while tracking is active.
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingActivity
    : AppCompatActivity(), ServiceConnection {

    private var trackingService: TrackingService? = null

    private var previousTrackingState = TrackingService.State.TRACKING_STOPPED
    private var currentTrackingState = TrackingService.State.TRACKING_STOPPED

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracking_activity)

        setupLockButtonStateChangedListener()

        startStopButton.setOnClickListener {
            trackingService?.let { service ->
                when(service.getCurrentState()) {
                    TrackingService.State.TRACKING_STOPPED ->
                        requestPermissionForAction(REQUEST_CODE_ACTION_START_TRACKING)

                    TrackingService.State.TRACKING_STARTED,
                    TrackingService.State.TRACKING_PAUSED ->
                        requestPermissionForAction(REQUEST_CODE_ACTION_STOP_TRACKING)
                }
            }
        }

        pauseResumeButton.setOnClickListener {
            trackingService?.let { service ->
                when(service.getCurrentState()) {
                    TrackingService.State.TRACKING_STARTED ->
                        requestPermissionForAction(REQUEST_CODE_ACTION_PAUSE_TRACKING)

                    TrackingService.State.TRACKING_PAUSED ->
                        requestPermissionForAction(REQUEST_CODE_ACTION_RESUME_TRACKING)

                    else ->
                        Log.e(TAG, "pauseResumeButton.onClick(): Unexpected service state (state=${service.state.value})")
                }
            }
        }

        bindService(Intent(this, TrackingService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionResult(requestCode=$requestCode, permissions=$permissions, grantResults=$grantResults) Called")

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            trackingService?.let {
                when (requestCode) {

                    //
                    // FIXME: Pass config object
                    //

                    REQUEST_CODE_ACTION_START_TRACKING  -> it.startTracking(TrackingService.Config())
                    REQUEST_CODE_ACTION_PAUSE_TRACKING  -> it.pauseTracking()
                    REQUEST_CODE_ACTION_RESUME_TRACKING -> it.resumeTracking()

                    REQUEST_CODE_ACTION_STOP_TRACKING -> {
                        AlertDialog.Builder(this).apply {
                            setTitle(R.string.tracking_stop_prompt_title)
                            setMessage(R.string.tracking_stop_prompt_message)

                            setPositiveButton(R.string.tracking_stop_prompt_save_button) { _, _ ->
                                it.stopTracking(true)

                                // TODO: Set return code to tell parent activity that the route was saved

                                finish()
                            }

                            setNegativeButton(R.string.tracking_stop_prompt_discard_button) { _, _ ->
                                it.stopTracking(false)

                                // TODO: Set return code to tell parent activity that the route was discarded

                                finish()
                            }

                            setNeutralButton(R.string.tracking_stop_prompt_cancel_button) { dialog, _ ->
                                dialog.cancel()
                            }

                            create().show()
                        }
                    }

                    else -> Log.e(TAG, "onRequestPermissionsResult(): Unexpected action request code (requestCode=$requestCode)")
                }
            }

        } else {
            Toast.makeText(this, R.string.error_no_location_permissions, Toast.LENGTH_SHORT).show()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected(name=$componentName) Called")

        (service as TrackingService.ServiceBinder).getService().also {

            it.state.observe(this, Observer { state ->
                previousTrackingState = currentTrackingState
                currentTrackingState = state

                onServiceStateChanged(previousTrackingState, currentTrackingState)
            })

            trackingService = it
        }

        startStopButton.isEnabled = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected(name=$componentName) Called")

        trackingService = null
        startStopButton.isEnabled = false
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun onServiceStateChanged(oldState: TrackingService.State, newState: TrackingService.State) {

        Log.i(TAG, "onServiceStateChanged(oldState=$oldState, newState=$newState): State Changed")

        when(newState) {
            TrackingService.State.TRACKING_STARTED -> {
                when(oldState) {
                    TrackingService.State.TRACKING_STOPPED -> {
                        startStopButton.setText(R.string.tracking_stop_button_title)
                        startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkRed))

                        pauseResumeButton.visibility = View.VISIBLE
                        lockButton.visibility = View.VISIBLE
                    }

                    TrackingService.State.TRACKING_PAUSED -> {
                        pauseResumeButton.setText(R.string.tracking_pause_button_title)
                    }

                    else -> {
                        Log.w(TAG, "onServiceStateChanged(oldState=$oldState, newState=$newState): Unexpected state change")
                    }
                }
            }

            TrackingService.State.TRACKING_PAUSED -> {
                pauseResumeButton.setText(R.string.tracking_resume_button_title)
            }

            TrackingService.State.TRACKING_STOPPED -> {
                startStopButton.setTextColor(R.string.tracking_start_button_title)
                startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkGreen))

                lockButton.visibility = View.GONE
                pauseResumeButton.visibility = View.GONE
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupLockButtonStateChangedListener() {

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

    private fun requestPermissionForAction(requestCode: Int) {
        Log.i(TAG, "requestPermissionForAction(requestCode=$requestCode): Called")

        //
        // On SDK 23 or later the user can revoke the applications permissions (those specified in the manifest) at anytime
        // so we need to check that the locationPoints permissions are still granted before we interact with the location manager
        // for location tracking requests.
        //
        // Note: If the application is running on a device with an SDK prior to 23 then we just fake the check as the permissions
        // will have been accepted during installation and cannot be revoked later.
        //

        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        if(Build.VERSION.SDK_INT >= 23
            && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            Log.d(TAG, "requestPermissionForAction(): Location permissions not granted")

            //
            // The user may have explicitly revoked the location permissions so we need to ask for them to be re-granted
            //

            ActivityCompat.requestPermissions(this, permissions, requestCode)

        } else {

            Log.d(TAG, "requestPermissionForAction(): Location permissions granted")

            //
            // We're either running on a device with SDK < 23 (So have permissions via manifest) or the user hasn't revoked the permissions
            //

            onRequestPermissionsResult(requestCode, permissions, intArrayOf(PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED))
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingActivity::class.java.simpleName

        private const val REQUEST_CODE_ACTION_START_TRACKING      = 1000
        private const val REQUEST_CODE_ACTION_PAUSE_TRACKING      = 1001
        private const val REQUEST_CODE_ACTION_RESUME_TRACKING     = 1002
        private const val REQUEST_CODE_ACTION_STOP_TRACKING       = 1003

        @JvmStatic
        fun newInstance(context: Context) {
            val intent = Intent(context, TrackingActivity::class.java)

            context.startActivity(intent)
        }
    }


}
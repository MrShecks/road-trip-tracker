package ie.justonetech.roadtriptracker.view.activities

import android.Manifest
import android.app.Activity
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
import com.google.android.material.snackbar.Snackbar
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.service.TrackingService
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.utils.ProfileType
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate() - Called")
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
                        requestStopTrackingWithPrompt(R.string.tracking_save_prompt_message)
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

        // TODO: Will still need to get the ProfileConfig for the current profile from the DB so
        // TODO: we can display the stats with the correct unit metrics.

        Preferences(this).currentProfile.also { profile ->
            profileName.setText(profile.nameId)
            profileName.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this, profile.drawableId),
                null,
                null,
                null
            )

            profileTag.setBackgroundColor(ContextCompat.getColor(this, profile.colorId))
        }
    }

    override fun onStart() {
        Log.i(TAG, "onStart() - Called")
        super.onStart()

        val serviceIntent = Intent(this, TrackingService::class.java)

        // We need to explicitly start the tracking service so that it remains running until
        // it is explicitly stopped. If we just bind to the service it will be stopped when
        // we unbind in onStop(). This will mean that the service will be stopped during
        // device configuration changes (e.g rotations) which we don't want. By explicitly
        // starting the service it will remain running through configuration changes and
        // will only be stopped when want to stop it (e.g TrackingService.stopTracking())

        startService(serviceIntent)
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.i(TAG, "onStop() - Called")
        super.onStop()

        // Unbind from the service when the activity is stopped.
        // Note: Because we explicitly started the service in onStart() it will remain
        // running and the activity will just re-bind when onStart() is called.

        unbindService(this)
    }

    override fun onBackPressed() {

        if(lockButton.locked) {
            // The screen is currently locked
            showLockMessage(this, R.string.tracking_screen_locked_message, true)

        } else if(trackingService != null && trackingService?.getCurrentState() != TrackingService.State.TRACKING_STOPPED) {
            requestStopTrackingWithPrompt(R.string.tracking_stop_and_save_prompt_message)

        } else {

            // If we get here it means that no route has been started so we just let the
            // calling activity know that no new route has been created and continue
            // with the back press call.

            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionResult(requestCode=$requestCode, permissions=$permissions, grantResults=$grantResults) Called")

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            trackingService?.let {
                when (requestCode) {

                    //
                    // FIXME: Pass valid config object
                    //

                    REQUEST_CODE_ACTION_START_TRACKING  -> {
                        it.startTracking(TrackingService.Config(
                            Preferences(this).currentProfile.id,
                            1.0f
                        ))
                    }

                    REQUEST_CODE_ACTION_PAUSE_TRACKING  -> it.pauseTracking()
                    REQUEST_CODE_ACTION_RESUME_TRACKING -> it.resumeTracking()

                    REQUEST_CODE_ACTION_STOP_TRACKING_SAVE -> {
                        it.stopTracking(true)

                        // FIXME: I might change how the application works and allow the user to start another tracking session without
                        // FIXME: exiting and re-entering the TrackingActivity, so this will most likely be removed.

                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    REQUEST_CODE_ACTION_STOP_TRACKING_DISCARD -> {
                        it.stopTracking(false)

                        // FIXME: I might change how the application works and allow the user to start another tracking session without
                        // FIXME: exiting and re-entering the TrackingActivity, so this will most likely be removed

                        setResult(Activity.RESULT_CANCELED)
                        finish()
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
                onServiceStateChanged(state)
            })

            trackingService = it
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected(name=$componentName) Called")

        trackingService = null
        startStopButton.isEnabled = false
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun onServiceStateChanged(newState: TrackingService.State) {

        Log.i(TAG, "onServiceStateChanged(newState=$newState): State Changed")

        when(newState) {
            TrackingService.State.TRACKING_STARTED -> {
                startStopButton.setText(R.string.tracking_stop_button_title)
                startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red_tracking_button_tint))

                pauseResumeButton.setText(R.string.tracking_pause_button_title)
                pauseResumeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green_tracking_button_tint))

                pauseResumeButton.visibility = View.VISIBLE
                lockButton.visibility = View.VISIBLE
            }

            TrackingService.State.TRACKING_STOPPED -> {
                startStopButton.setText(R.string.tracking_start_button_title)
                startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green_tracking_button_tint))

                pauseResumeButton.visibility = View.GONE
                lockButton.visibility = View.GONE
            }

            TrackingService.State.TRACKING_PAUSED -> {
                startStopButton.setText(R.string.tracking_stop_button_title)
                startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red_tracking_button_tint))

                pauseResumeButton.setText(R.string.tracking_resume_button_title)
                pauseResumeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.orange_tracking_button_tint))

                pauseResumeButton.visibility = View.VISIBLE
                lockButton.visibility = View.VISIBLE
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

    private fun requestStopTrackingWithPrompt(@StringRes messageId: Int) {

        AlertDialog.Builder(this).apply {
            setTitle(R.string.tracking_stop_prompt_title)
            setMessage(messageId)

            setPositiveButton(R.string.tracking_stop_prompt_save_button) { _, _ ->
                requestPermissionForAction(REQUEST_CODE_ACTION_STOP_TRACKING_SAVE)
            }

            setNegativeButton(R.string.tracking_stop_prompt_discard_button) { _, _ ->
                requestPermissionForAction(REQUEST_CODE_ACTION_STOP_TRACKING_DISCARD)
            }

            setNeutralButton(R.string.tracking_stop_prompt_cancel_button) { dialog, _ ->
                dialog.cancel()
            }

            create().show()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingActivity::class.java.simpleName

        private const val REQUEST_CODE_ACTION_START_TRACKING            = 1000
        private const val REQUEST_CODE_ACTION_PAUSE_TRACKING            = 1001
        private const val REQUEST_CODE_ACTION_RESUME_TRACKING           = 1002
        private const val REQUEST_CODE_ACTION_STOP_TRACKING_SAVE        = 1003
        private const val REQUEST_CODE_ACTION_STOP_TRACKING_DISCARD     = 1004

        @JvmStatic
        fun newInstance(context: Context) {
            val intent = Intent(context, TrackingActivity::class.java)

            context.startActivity(intent)
        }
    }


}
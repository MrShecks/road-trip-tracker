/*
 * This file is part of Road Trip Tracker.
 *
 * Road Trip Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Road Trip Tracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Road Trip Tracker.  If not, see <https://www.gnu.org/licenses/>.
 */

package ie.justonetech.roadtriptracker.view.activities

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.service.GeoLocation
import ie.justonetech.roadtriptracker.model.TrackingStats
import ie.justonetech.roadtriptracker.service.TrackingService
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.view.fragments.tracking.DashFragmentFactory
import ie.justonetech.roadtriptracker.view.utils.addFragment
import ie.justonetech.roadtriptracker.view.utils.findFragmentById
import ie.justonetech.roadtriptracker.view.utils.getRotation
import ie.justonetech.roadtriptracker.view.widgets.ImageToast
import ie.justonetech.roadtriptracker.view.widgets.LockButton
import ie.justonetech.roadtriptracker.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.tracking_activity.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingActivity
// Active route tracking active. Starts/stops route tracking service and displays live stats
// and route progress while tracking is active.
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingActivity
    : AppCompatActivity(), ServiceConnection {

    private var trackingService: TrackingService? = null
    private var currentProfileConfig: ProfileConfig? = null

    private var map: GoogleMap? = null

    private var currentLocationMarker: Marker? = null
    private var currentAccuracyCircle: Circle? = null

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracking_activity)

        // Hide the action bar when in landscape orientation

        when(getRotation()) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                supportActionBar?.hide()

                // FIXME: Not sure if I want to hide the status bar or not
                //window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }

        mapView?.let {
            it.onCreate(savedInstanceState)
            it.getMapAsync { map ->
                map.mapType = Preferences(this).mapType
                this.map = map
            }
        }

        setupProfileDashFragment(Preferences(this).currentProfile)
        setupLockButtonStateChangedListener()

        startStopButton.setOnClickListener {
            trackingService?.let { service ->
                when(service.getCurrentState()) {
                    TrackingService.State.TRACKING_STOPPED -> {
                        if(!isLocationServiceAvailable()) {
                            requestStartLocationServices()

                        } else {
                            requestPermissionForAction(REQUEST_CODE_ACTION_START_TRACKING)
                        }
                    }


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
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()

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
        super.onStop()
        mapView?.onStop()

        // Unbind from the service when the activity is stopped.
        // Note: Because we explicitly started the service in onStart() it will remain
        // running and the activity will just re-bind when onStart() is called.

        unbindService(this)
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        //
        // Restore the screen lock state based on the lock button
        //

        setScreenLockState(lockButton.isLocked)
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {

        if(lockButton.isLocked) {
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
                    REQUEST_CODE_ACTION_START_TRACKING  -> {
                        check(currentProfileConfig != null) { "|currentProfileConfig| has not be retrieved from database" }

                        currentProfileConfig?.let { profileConfig ->
                            it.startTracking(TrackingService.Config(
                                profileConfig.id,
                                profileConfig.sampleInterval,
                                profileConfig.statUpdateInterval
                            ))
                        }
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

            it.liveStats.observe(this, Observer { stats ->
                onServiceStatsChanged(stats)
            })

            it.currentLocation.observe(this, Observer { location ->
                onServiceLocationChanged(location)
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

                if(Preferences(this).keepScreenOn)
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            TrackingService.State.TRACKING_STOPPED -> {
                startStopButton.setText(R.string.tracking_start_button_title)
                startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green_tracking_button_tint))

                pauseResumeButton.visibility = View.GONE
                lockButton.visibility = View.GONE

                if(Preferences(this).keepScreenOn)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

    private fun onServiceStatsChanged(stats: TrackingStats) {
        findFragmentById<DashFragmentFactory.DashFragment>(R.id.dashFragmentContainer)?.updateStats(stats)
    }

    private fun onServiceLocationChanged(location: GeoLocation) {
        Log.i(TAG, "onServiceLocationChanged(newLocation=$location): Location Changed")

        map?.let {
            if(currentAccuracyCircle == null) {
                currentAccuracyCircle = it.addCircle(
                    CircleOptions()
                        .center(location.latLng)
                        .fillColor(ContextCompat.getColor(this, R.color.mapAccuracyCircleColor))
                        .strokeColor(ContextCompat.getColor(this, R.color.mapAccuracyCircleColor))
                        .strokeWidth(0.0f)
                        .radius(location.accuracy.toDouble())
                )

            } else {
                currentAccuracyCircle?.center = location.latLng
            }

            if (currentLocationMarker == null) {
                currentLocationMarker = it.addMarker(
                    MarkerOptions()
                        .position(location.latLng)
                        .anchor(0.5f, 0.5f)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker))
                )

                it.animateCamera(CameraUpdateFactory.newLatLngZoom(location.latLng, 17.5f))

            } else {
                currentLocationMarker?.position = location.latLng
                it.animateCamera(CameraUpdateFactory.newLatLng(location.latLng))
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupProfileDashFragment(profileType: ProfileType) {

        // Add dash fragment based on the selected profile
        addFragment(R.id.dashFragmentContainer, DashFragmentFactory.newInstance(profileType))

        ViewModelProvider(this).get(ProfileViewModel::class.java).also { model ->
            model.profile.observe(this, Observer { profileConfig ->

                findFragmentById<DashFragmentFactory.DashFragment>(R.id.dashFragmentContainer)?.setProfile(profileConfig)

                profileName?.setText(profileType.nameId)
                profileName?.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(this@TrackingActivity, profileType.drawableId),
                    null,
                    null,
                    null
                )

                profileTag?.setBackgroundColor(profileType.getColor(this@TrackingActivity))

                currentProfileConfig = profileConfig
            })

            model.getProfile(profileType)
        }
    }

    private fun setupLockButtonStateChangedListener() {

        lockButton.setOnLockStateChangedListener(object: LockButton.LockStateChangedListener{
            override fun onBeginLock(button: LockButton) {
                lockProgress.progress = 0
                lockProgress.visibility = View.VISIBLE
            }

            override fun onEndLock(button: LockButton) {
                lockProgress.visibility = View.INVISIBLE
                showLockMessage(this@TrackingActivity, button.isLocked)
            }

            override fun onCancelLock(button: LockButton) {
                lockProgress.visibility = View.INVISIBLE
            }

            override fun onLockProgress(button: LockButton, progress: Int) {
                lockProgress.progress = progress
            }

            override fun onLockStateChanged(buttons: LockButton, isLocked: Boolean) {
                setScreenLockState(isLocked)
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

    private fun setScreenLockState(isLocked: Boolean) {
        Log.i(TAG, "onRestoreInstanceState(): LockButton.isLocked=$isLocked")

        startStopButton.isEnabled = !isLocked
        pauseResumeButton.isEnabled = !isLocked
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

    private fun requestStartLocationServices() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.tracking_location_services_prompt_title)
            setMessage(R.string.tracking_location_services_prompt_message)

            setPositiveButton(android.R.string.yes) { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

            setNegativeButton(android.R.string.no) { _, _ ->
                // Nothing to do here...
            }

            create().show()
        }
    }

    private fun isLocationServiceAvailable(): Boolean {
        return try {
            with(getSystemService(Context.LOCATION_SERVICE) as LocationManager) {
                isProviderEnabled(LocationManager.GPS_PROVIDER)
            }

        } catch (_: SecurityException) {
            false
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
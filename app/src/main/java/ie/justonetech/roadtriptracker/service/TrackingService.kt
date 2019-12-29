package ie.justonetech.roadtriptracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.TrackingRepository
import ie.justonetech.roadtriptracker.model.db.entities.DbRouteDetail
import ie.justonetech.roadtriptracker.model.db.entities.DbRoutePoint
import ie.justonetech.roadtriptracker.view.activities.TrackingActivity

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingService
// Local foreground service used to monitor location service updates to track position
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingService : Service() {

    enum class State {
        TRACKING_STARTED,
        TRACKING_PAUSED,
        TRACKING_STOPPED
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Config
    // Configuration settings passed to the service when starting a new tracking session
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    data class Config(
        val profileId: Int,
        val sampleInterval: Float
    )

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ServiceBinder
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    inner class ServiceBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val state: LiveData<State> = MutableLiveData<State>(State.TRACKING_STOPPED)

    private val serviceBinder = ServiceBinder()
    private val trackingState = TrackingState()

    private var currentAirPressure: Float = Float.NaN

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val locationCallback: LocationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            locationResult?.lastLocation?.let {
                val barometricAltitude = getBarometricAltitude(currentAirPressure)

                trackingState.update(LocationFix(it, barometricAltitude))

                Log.i(TAG, "onLocationResult(): Location Fix=$it, Barometric Altitude=$barometricAltitude, currentAirPressure=$currentAirPressure")
            }

            super.onLocationResult(locationResult)
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val pressureSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            currentAirPressure = event?.values?.first() ?: Float.NaN
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not used
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate() {
        Log.i(TAG, "Service onCreate() called")

        Toast.makeText(this, "$TAG:onCreate() Called", Toast.LENGTH_LONG).show()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Note: getSystemService() can return a null object. It's unlikely but it's probably
            // safer to treat the object as nullable and use Kotlin's "let" function
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.let {
                val appName = getString(R.string.app_name)
                val channel = NotificationChannel(NOTIFICATION_CHANNEL, appName, NotificationManager.IMPORTANCE_DEFAULT)

                channel.description = appName
                it.createNotificationChannel(channel)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy() called")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Note: getSystemService() can return a null object. It's unlikely but it's probably
            // safer to treat the object as nullable and use Kotlin's safe call operator
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.deleteNotificationChannel(NOTIFICATION_CHANNEL)
        }

        Toast.makeText(this, "$TAG:onDestroy() Called", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return serviceBinder
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun startTracking(config: Config) {
        check(state.value == State.TRACKING_STOPPED) {
            "Tracking service can only be started from the stopped state (state=${state.value})"
        }

        trackingState.start(config.profileId, config.sampleInterval)

        startSensorListeners()
        startLocationListener()

        showServiceNotification()

        setServiceState(State.TRACKING_STARTED)
    }

    fun stopTracking(saveRoute: Boolean = true) {
        check(state.value == State.TRACKING_STARTED || state.value == State.TRACKING_PAUSED) {
            "Tracking service can only be stopped from the started or paused state (state=${state.value})"
        }

        stopLocationListener()
        stopSensorListeners()

        removeServiceNotification()
        trackingState.stop()

        if(saveRoute) {
            TrackingRepository(this).addRoute(
                DbRouteDetail(
                    null,
                    trackingState.profileId,
                    trackingState.totalDuration.startTime,
                    trackingState.totalDuration.endTime,
                    trackingState.totalDuration.getElapsedTime(),
                    trackingState.activeDuration.getElapsedTime(),
                    trackingState.distance,
                    trackingState.maxSpeed,
                    trackingState.avgSpeed,
                    trackingState.avgActiveSpeed,
                    false,

                    // FIXME: OCD - Move to before isFavourite after next DB drop
                    trackingState.maxElevationGain,
                    trackingState.totalElevationGain
                ),

                trackingState.locationPoints.map {
                    DbRoutePoint(
                        null,
                        0,      // Route ID will be set during Db insert

                        it.time,

                        it.latitude,
                        it.longitude,
                        it.getGpsAltitude(),

                        it.speed,
                        it.bearing,

                        it.getBarometricAltitude()
                    )
                }
            )
        }

        setServiceState(State.TRACKING_STOPPED)
        stopSelf()
    }

    fun pauseTracking() {
        check(state.value == State.TRACKING_STARTED) {
            "Tracking service can only be paused from the started state (state=${state.value})"
        }

        // TODO: I might want to add an auto stop/start based on movement in the future, if so I will need
        // TODO: to keep listening for location fixes while tracking is paused so that I can detect when
        // TODO: to auto re-start tracking.

        trackingState.pause()

        stopLocationListener()
        stopSensorListeners()

        setServiceState(State.TRACKING_PAUSED)
    }

    fun resumeTracking() {
        check(state.value == State.TRACKING_PAUSED) {
            "Tracking service can only be resumed from the paused state (state=${state.value})"
        }

        trackingState.resume()

        startLocationListener()
        startSensorListeners()

        setServiceState(State.TRACKING_STARTED)
    }

    fun getCurrentState(): State = state.value ?: State.TRACKING_STOPPED

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showServiceNotification() {
        val text = getText(R.string.tracking_service_tracking_started_message)
        val intent = Intent(this, TrackingActivity::class.java)

        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // TODO: Look into adding Pause/Resume and Stop actions to the notification. When paused see if we can
        // TODO: also display the paused timer in the notification.

        // Note: On Android 8.0 or later the service needs to run in the foreground and display a persistent
        // notification in order to receive timely location updates and avoid being limited as a background
        // service. (Ref: https://developer.android.com/guide/components/services#java)

        // Set the info for the views that show in the notification panel.
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL).apply {

            setSmallIcon(R.drawable.ic_service_notification_white_24dp)    // the status icon
            setTicker(text)                                                // the status text
            setContentText(text)                                           // the contents of the entry
            setContentTitle(getText(R.string.app_name))                           // the label of the entry
            setWhen(System.currentTimeMillis())                            // the time stamp
            setContentIntent(contentIntent)                                // The intent to send when the entry is clicked
            setOngoing(true)

        }.build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun removeServiceNotification() {
        // Note: On Android 8.0 or later the service needs to run in the foreground in order to receive
        // timely location updates. When we are destroyed we need to remove our foreground notification.

        stopForeground(true)
    }

    private fun startLocationListener() {
        val locationService = LocationServices.getFusedLocationProviderClient(this)

        with(LocationRequest.create()) {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            if(ContextCompat.checkSelfPermission(this@TrackingService, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED)
                locationService.requestLocationUpdates(this, locationCallback, android.os.Looper.myLooper())
        }
    }

    private fun stopLocationListener() {
        val locationService = LocationServices.getFusedLocationProviderClient(this)

        locationService.removeLocationUpdates(locationCallback)
    }

    private fun startSensorListeners() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.let {
            sensorManager.registerListener(pressureSensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopSensorListeners() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.unregisterListener(pressureSensorListener)
    }

    private fun setServiceState(newState: State) {
        check(newState != state.value) { "Attempt to set service state to current state (newState=$newState, oldState=${state.value})" }

        (state as MutableLiveData).value = newState
    }

    private fun getBarometricAltitude(pressure: Float) : Double {
        return if(!pressure.isNaN())
            SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure).toDouble()
        else
            0.0
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingService::class.java.simpleName

        private const val NOTIFICATION_ID                   = 1
        private const val NOTIFICATION_CHANNEL              = "Road Trip Tracker Notifications"

        private const val UPDATE_INTERVAL: Long             = 10000
        private const val FASTEST_UPDATE_INTERVAL: Long     = 5000
    }
}
package ie.justonetech.roadtriptracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ie.justonetech.roadtriptracker.R
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
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    class Config {
        // TODO: Add tracking config
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Stats
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    class Stats {

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ServiceBinder
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    inner class ServiceBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val state: LiveData<State> by lazy { trackingState }

    private val serviceBinder = ServiceBinder()
    private val trackingState = MutableLiveData<State>(State.TRACKING_STOPPED)

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
        Log.i(TAG, "onStartCommand() Called: startId=$startId")


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
        check(trackingState.value == State.TRACKING_STOPPED) {
            "Tracking service can only be started from the stopped state (state=$trackingState)"
        }

        showServiceNotification()
        setServiceState(State.TRACKING_STARTED)
    }

    fun stopTracking(saveRoute: Boolean = true) {
        check(trackingState.value == State.TRACKING_STARTED || trackingState.value == State.TRACKING_PAUSED) {
            "Tracking service can only be stopped from the started or paused state (state=$trackingState)"
        }

        removeServiceNotification()
        setServiceState(State.TRACKING_STOPPED)
    }

    fun pauseTracking() {
        check(trackingState.value == State.TRACKING_STARTED) {
            "Tracking service can only be paused from the started state (state=$trackingState)"
        }

        setServiceState(State.TRACKING_PAUSED)
    }

    fun resumeTracking() {
        check(trackingState.value == State.TRACKING_PAUSED) {
            "Tracking service can only be resumed from the paused state (state=$trackingState)"
        }

        setServiceState(State.TRACKING_STARTED)
    }

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
            setContentTitle(getText(R.string.app_name))                    // the label of the entry
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

    private fun setServiceState(newState: State) {
        check(newState != trackingState.value) { "Attempt to set service state to current state (newState=$newState, oldState=${trackingState.value})" }

        trackingState.value = newState
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingService::class.java.simpleName

        private const val NOTIFICATION_ID                   = 1
        private const val NOTIFICATION_CHANNEL              = "Road Trip Tracker Notifications"
    }
}
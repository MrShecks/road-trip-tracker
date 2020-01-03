package ie.justonetech.roadtriptracker.utils

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager
import androidx.core.content.edit
import com.google.android.gms.maps.GoogleMap
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// Preferences
// Singleton used to manage the applications stored preferences
///////////////////////////////////////////////////////////////////////////////////////////////////

class Preferences private constructor(context: Context) {

    var currentProfile: ProfileType
        get() {
            return ProfileType.fromId(
                prefs.getInt(PREF_KEY_CURRENT_PROFILE,
                    ProfileType.PROFILE_TYPE_WALKING.id)
            )
        }

        set(value) {
            prefs.edit(true) {
                putInt(PREF_KEY_CURRENT_PROFILE, value.id)
            }
        }

    val keepScreenOn: Boolean
        get() = prefs.getBoolean(PREF_KEY_KEEP_SCREEN_ON, true)

    val mapType: Int
        get() {
            return mapTypes[prefs.getString(PREF_KEY_MAP_TYPE, "")!!]
                ?: GoogleMap.MAP_TYPE_NORMAL
        }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    //
    // Preference key names
    //

    private val PREF_KEY_CURRENT_PROFILE        = context.getString(R.string.pref_key_current_profile)
    private val PREF_KEY_KEEP_SCREEN_ON         = context.getString(R.string.pref_key_keep_screen_on)
    private val PREF_KEY_MAP_TYPE               = context.getString(R.string.pref_key_map_type)

    //
    // Preference values
    //

    private val mapTypes = mapOf(
        context.getString(R.string.pref_value_map_type_normal) to GoogleMap.MAP_TYPE_NORMAL,
        context.getString(R.string.pref_value_map_type_terrain) to GoogleMap.MAP_TYPE_TERRAIN,
        context.getString(R.string.pref_value_map_type_satellite) to GoogleMap.MAP_TYPE_SATELLITE,
        context.getString(R.string.pref_value_map_type_hybrid) to GoogleMap.MAP_TYPE_HYBRID
    )

    companion object {
        private val TAG = Preferences::class.java.simpleName

        @Volatile
        private var instance: Preferences? = null
        private var LOCK = Any()

        @JvmStatic
        operator fun invoke(context: Context): Preferences {
            return instance ?: synchronized(LOCK) {
                instance ?: Preferences(context).also {
                    instance = it
                }
            }
        }
    }
}
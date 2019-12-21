package ie.justonetech.roadtriptracker.utils

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager
import androidx.core.content.edit

///////////////////////////////////////////////////////////////////////////////////////////////////
// Preferences
// Singleton used to manage the applications stored preferences
///////////////////////////////////////////////////////////////////////////////////////////////////

class Preferences private constructor(context: Context) {

    var currentProfile: ProfileType
        get() {
            return ProfileType.fromId(
                prefs.getInt(PREF_KEY_PROFILE_TYPE,
                    ProfileType.PROFILE_TYPE_CYCLING.id)
            )
        }

        set(value) {
            prefs.edit(true) {
                putInt(PREF_KEY_PROFILE_TYPE, value.id)
            }
        }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    companion object {
        private val TAG = Preferences::class.java.simpleName

        private const val PREF_KEY_PROFILE_TYPE = "_pref_profile_type"

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
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

package ie.justonetech.roadtriptracker.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo

import android.preference.PreferenceManager
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.google.android.gms.maps.GoogleMap
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// Preferences
// Singleton used to manage the applications stored preferences
///////////////////////////////////////////////////////////////////////////////////////////////////

class Preferences private constructor(private val context: Context) {

    var currentProfile: ProfileType
        get() {
            return ProfileType.fromId(
                getInt(R.string.pref_key_current_profile, ProfileType.PROFILE_TYPE_WALKING.id)
            )
        }

        set(value) = putInt(R.string.pref_key_current_profile, value.id)

    val keepScreenOn: Boolean
        get() = getBoolean(R.string.pref_key_keep_screen_on, true)

    val mapType: Int
        get() = mapTypes[getString(R.string.pref_key_map_type, "")] ?: GoogleMap.MAP_TYPE_NORMAL

    var trackingScreenOrientation: Int
        get() = getInt(R.string.pref_key_tracking_screen_orientation, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        set(value) = putInt(R.string.pref_key_tracking_screen_orientation, value)

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    //
    // Preference key names
    //

//    private val PREF_KEY_CURRENT_PROFILE                = context.getString(R.string.pref_key_current_profile)
//    private val PREF_KEY_KEEP_SCREEN_ON                 = context.getString(R.string.pref_key_keep_screen_on)
//    private val PREF_KEY_MAP_TYPE                       = context.getString(R.string.pref_key_map_type)
//    private val PREF_KEY_TRACKING_SCREEN_ORIENTAITON    = context.getString(R.string.pref_key_tracking_screen_orientation)

    //
    // Preference values
    //

    private val mapTypes = mapOf(
        context.getString(R.string.pref_value_map_type_normal) to GoogleMap.MAP_TYPE_NORMAL,
        context.getString(R.string.pref_value_map_type_terrain) to GoogleMap.MAP_TYPE_TERRAIN,
        context.getString(R.string.pref_value_map_type_satellite) to GoogleMap.MAP_TYPE_SATELLITE,
        context.getString(R.string.pref_value_map_type_hybrid) to GoogleMap.MAP_TYPE_HYBRID
    )

    private fun getString(@StringRes id: Int, defaultValue: String = ""): String {
        return prefs.getString(context.getString(id), defaultValue) ?: defaultValue
    }

    private fun putString(@StringRes id: Int, value: String, commit: Boolean = true) {
        prefs.edit(commit) {
            putString(context.getString(id), value)
        }
    }

    private fun getBoolean(@StringRes id: Int, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(context.getString(id), defaultValue)
    }

    private fun putBoolean(@StringRes id: Int, value: Boolean, commit: Boolean = true) {
        prefs.edit(commit) {
            putBoolean(context.getString(id), value)
        }
    }

    private fun getInt(@StringRes id: Int, defaultValue: Int): Int {
        return prefs.getInt(context.getString(id), defaultValue)
    }

    private fun putInt(@StringRes id: Int, value: Int, commit: Boolean = true) {
        prefs.edit(commit) {
            putInt(context.getString(id), value)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = Preferences::class.java.simpleName

        @Volatile
        private var instance: Preferences? = null
        private var LOCK = Any()

        @JvmStatic
        operator fun invoke(context: Context): Preferences {
            return instance ?: synchronized(LOCK) {
                instance ?: Preferences(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
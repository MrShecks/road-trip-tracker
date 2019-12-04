package ie.justonetech.roadtriptracker.utils

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileType
// Supported profile types and metadata
///////////////////////////////////////////////////////////////////////////////////////////////////

enum class ProfileType(val id: Int, @DrawableRes val drawableId: Int, @StringRes val nameId: Int) {

    PROFILE_TYPE_WALKING(1000, R.drawable.ic_profile_walking_black_24dp, R.string.app_name),
    PROFILE_TYPE_RUNNING(1001, R.drawable.ic_profile_running_black_24dp, R.string.app_name),
    PROFILE_TYPE_CYCLING(1002, R.drawable.ic_profile_cycling_black_24dp, R.string.app_name),
    PROFILE_TYPE_DRIVING(1003, R.drawable.ic_profile_driving_black_24dp, R.string.app_name),
    PROFILE_TYPE_BOATING(1004, R.drawable.ic_profile_boating_black_24dp, R.string.app_name),
    PROFILE_TYPE_MOTORCYCLING(1005, R.drawable.ic_profile_motorcycling_black_24dp, R.string.app_name);

    fun getName(context: Context): String {
        return context.getString(nameId)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        @JvmStatic
        fun fromId(id: Int, defaultValue: ProfileType = PROFILE_TYPE_WALKING): ProfileType {
            return when(id) {
                PROFILE_TYPE_WALKING.id         -> PROFILE_TYPE_WALKING
                PROFILE_TYPE_RUNNING.id         -> PROFILE_TYPE_RUNNING
                PROFILE_TYPE_CYCLING.id         -> PROFILE_TYPE_CYCLING
                PROFILE_TYPE_DRIVING.id         -> PROFILE_TYPE_DRIVING
                PROFILE_TYPE_BOATING.id         -> PROFILE_TYPE_BOATING
                PROFILE_TYPE_MOTORCYCLING.id    -> PROFILE_TYPE_MOTORCYCLING
                else                            -> defaultValue
            }
        }
    }
}
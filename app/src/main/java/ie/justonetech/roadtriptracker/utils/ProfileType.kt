package ie.justonetech.roadtriptracker.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import ie.justonetech.roadtriptracker.R

///////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileType
// Supported profile types and metadata
///////////////////////////////////////////////////////////////////////////////////////////////////

enum class ProfileType(val id: Int, @DrawableRes val drawableId: Int, @StringRes val nameId: Int, @ColorRes val colorId: Int) {

    // Note: The enum 'id' value is stored in application database
    // so care should be taken to keep the ids consistent when
    // adding or removing enums

    PROFILE_TYPE_WALKING(1000, R.drawable.ic_profile_walking_white_24dp, R.string.profile_name_walking, R.color.colorProfile_Walking),
    PROFILE_TYPE_RUNNING(1001, R.drawable.ic_profile_running_white_24dp, R.string.profile_name_running, R.color.colorProfile_Running),
    PROFILE_TYPE_CYCLING(1002, R.drawable.ic_profile_cycling_white_24dp, R.string.profile_name_cycling, R.color.colorProfile_Cycling),
    PROFILE_TYPE_DRIVING(1003, R.drawable.ic_profile_driving_white_24dp, R.string.profile_name_driving, R.color.colorProfile_Driving),
    PROFILE_TYPE_BOATING(1004, R.drawable.ic_profile_boating_white_24dp, R.string.profile_name_boating, R.color.colorProfile_Boating),
    PROFILE_TYPE_MOTORCYCLING(1005, R.drawable.ic_profile_motorcycling_white_24dp, R.string.profile_name_motorcycling, R.color.colorProfile_Motorcycling);

    fun getName(context: Context): String {
        return context.getString(nameId)
    }

    @ColorInt
    fun getColor(context: Context): Int {
        return ContextCompat.getColor(context, colorId)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {

        @JvmStatic
        fun fromId(id: Int, defaultValue: ProfileType = PROFILE_TYPE_WALKING): ProfileType {
            return values().firstOrNull { it.id == id } ?: defaultValue
        }
    }
}
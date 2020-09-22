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

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter

///////////////////////////////////////////////////////////////////////////////////////////////////
// Utility functions used to bind model data to views/layouts
///////////////////////////////////////////////////////////////////////////////////////////////////

@BindingAdapter("android:profileImage")
fun setProfileImage(imageView: AppCompatImageView, profileId: Int) {
    imageView.setImageResource(ProfileType.fromId(profileId).drawableId)
}

@BindingAdapter("android:profileDrawable")
fun setProfileDrawable(textView: TextView, profileId: Int) {

    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
        textView,
        AppCompatResources.getDrawable(
            textView.context,
            ProfileType.fromId(profileId).drawableId
        ),
        null,
        null,
        null
    )
}

@BindingAdapter("android:profileColor")
fun setProfileTagColor(view: View, color: Int) {
    view.setBackgroundColor(color)
}

@BindingAdapter("android:goneIf")
fun setGoneIf(view: View, gone: Boolean) {
    view.visibility = if(gone) View.GONE else View.VISIBLE
}
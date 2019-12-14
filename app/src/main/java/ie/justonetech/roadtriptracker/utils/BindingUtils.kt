package ie.justonetech.roadtriptracker.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter

///////////////////////////////////////////////////////////////////////////////////////////////////
// Utility functions used to bind model data to views/layouts
///////////////////////////////////////////////////////////////////////////////////////////////////

@BindingAdapter("android:profileImage")
fun setProfileImage(imageView: ImageView, profileId: Int) {
    imageView.setImageResource(ProfileType.fromId(profileId).drawableId)
}

@BindingAdapter("android:profileDrawable")
fun setProfileDrawable(textView: TextView, profileId: Int) {

    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
        textView,
        ResourcesCompat.getDrawable(
            textView.resources,
            ProfileType.fromId(profileId).drawableId, null
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
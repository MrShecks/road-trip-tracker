package ie.justonetech.roadtriptracker.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

///////////////////////////////////////////////////////////////////////////////////////////////////
// Utility functions used to bind model data to views/layouts
///////////////////////////////////////////////////////////////////////////////////////////////////

@BindingAdapter("android:profileImage")
fun setProfileImage(imageView: ImageView, profileId: Int) {
    imageView.setImageResource(ProfileType.fromId(profileId).drawableId)
}

@BindingAdapter("android:goneIf")
fun setGoneIf(view: View, gone: Boolean) {
    view.visibility = if(gone) View.GONE else View.VISIBLE
}
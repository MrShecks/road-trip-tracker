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

package ie.justonetech.roadtriptracker.view.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import ie.justonetech.roadtriptracker.R
import kotlinx.android.synthetic.main.image_toast.view.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// ImageToast
// Custom message toast with image and text message
///////////////////////////////////////////////////////////////////////////////////////////////////

class ImageToast(context: Context) : Toast(context) {

    private val imageView: ImageView
    private val textView: TextView

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        LayoutInflater.from(context).inflate(R.layout.image_toast, null).let {
            imageView = it.toastImage
            textView = it.toastText
            view = it.toastContainer
        }
    }

    override fun setText(@StringRes resourceId: Int) {
        textView.setText(resourceId)
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setImageResource(@DrawableRes resourceId: Int) {
        imageView.setImageResource(resourceId)
    }

    fun setImageDrawable(image: Drawable) {
        imageView.setImageDrawable(image)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = ImageToast::class.java.simpleName

        @JvmStatic
        fun create(context: Context, @DrawableRes imageId: Int, @StringRes stringId: Int, duration: Int): ImageToast {
            return create(context, imageId, context.getString(stringId), duration)
        }

        @JvmStatic
        fun create(context: Context, @DrawableRes imageId: Int, text: String, duration: Int): ImageToast {
            return create(context, AppCompatResources.getDrawable(context, imageId), text, duration)
        }

        @JvmStatic
        private fun create(context: Context, image: Drawable?, text: String, duration: Int): ImageToast {
            val imageToast = ImageToast(context)

            if(image != null)
                imageToast.setImageDrawable(image)

            imageToast.setText(text)
            imageToast.duration = duration

            return imageToast
        }
    }
}
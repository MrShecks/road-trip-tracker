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
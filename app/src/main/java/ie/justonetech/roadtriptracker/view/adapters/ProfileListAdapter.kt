package ie.justonetech.roadtriptracker.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteProfile
import ie.justonetech.roadtriptracker.utils.ProfileType
import kotlinx.android.synthetic.main.profile_selection_list_item.view.*

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileListAdapter
// Adapter used when presenting a list of supported profiles to the user for selection
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class ProfileListAdapter(context: Context)
    : ArrayAdapter<RouteProfile>(context, R.layout.profile_selection_list_item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)

        //
        // FIXME: Do we really need to implement the ViewHolder pattern for 6 list items?
        //

        return layoutInflater.inflate(R.layout.profile_selection_list_item, parent, false).apply {
            getItem(position)?.let {
                val profileType = ProfileType.fromId(it.id)

                profileName.setText(profileType.nameId)
                profileName.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, profileType.drawableId), null, null, null
                )

                profileTag.setBackgroundColor(it.tagColor)
            }
        }
    }
}
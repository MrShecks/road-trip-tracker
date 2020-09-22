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

package ie.justonetech.roadtriptracker.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.utils.ProfileType
import kotlinx.android.synthetic.main.profile_selection_list_item.view.*

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileListAdapter
// Adapter used when presenting a list of supported profiles to the user for selection
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class ProfileListAdapter(context: Context)
    : ArrayAdapter<ProfileType>(context,0, ProfileType.values()) {

    private val layoutInflater = LayoutInflater.from(context)

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ProfileViewHolder
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    data class ProfileViewHolder(
        val profileTag: View,
        val profileName: TextView
    )

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View
        val viewHolder: ProfileViewHolder

        //
        // Ah ... manually implementing the ViewHolder pattern ... it's been a while
        //
        // The adapter currently only hold 6 items but lint was complaining about
        // not using the ViewHolder pattern to recycle views for efficiency.
        //
        // This is what we did before RecyclerView.Adapter kids ;)
        //

        if(convertView == null) {
            itemView = layoutInflater.inflate(R.layout.profile_selection_list_item, parent, false)
            viewHolder = ProfileViewHolder(itemView.profileTag, itemView.profileName)
            itemView.tag = viewHolder

        } else {
            itemView = convertView
            viewHolder = convertView.tag as ProfileViewHolder
        }

        return itemView.also {
            getItem(position)?.let { profile ->
                viewHolder.profileName.setText(profile.nameId)
                viewHolder.profileName.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, profile.drawableId), null, null, null
                )

                viewHolder.profileTag.setBackgroundColor(profile.getColor(parent.context))
            }
        }
    }
}
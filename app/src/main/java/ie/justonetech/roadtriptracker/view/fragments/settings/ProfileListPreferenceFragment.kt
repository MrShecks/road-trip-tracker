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

package ie.justonetech.roadtriptracker.view.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.adapters.ProfileListAdapter
import kotlinx.android.synthetic.main.profile_list_preference_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// ProfileListPreferenceFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class ProfileListPreferenceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_list_preference_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileList.adapter = ProfileListAdapter(view.context).also { profileListAdapter ->
            profileList.setOnItemClickListener { _, _, position, _ ->
                profileListAdapter.getItem(position)?.let { profileType ->
                    ProfileListPreferenceFragmentDirections.actionDestinationProfileListSelectionToProfilePreferences(profileType).also { action->
                        Navigation.findNavController(view).navigate(action)
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = ProfileListPreferenceFragment::class.java.simpleName
    }
}
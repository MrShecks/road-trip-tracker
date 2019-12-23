package ie.justonetech.roadtriptracker.view.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.view.activities.TrackingActivity
import ie.justonetech.roadtriptracker.view.adapters.ProfileListAdapter
import kotlinx.android.synthetic.main.home_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HomeFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class HomeFragment : MapViewHostFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState, routeMap)

        routeMap.onCreate(savedInstanceState)
        profileCardView.setOnClickListener {
            val profileListAdapter = ProfileListAdapter(it.context)

            AlertDialog.Builder(it.context).apply {
                setTitle(R.string.home_fragment_profile_dialog_title)
                setAdapter(profileListAdapter) { _, which ->
                    profileListAdapter.getItem(which)?.let { selectedProfile ->
                        Preferences(context).currentProfile = selectedProfile
                        showCurrentProfile(context, selectedProfile)
                    }
                }

                show()
            }
        }

        startTracking.setOnClickListener {
            TrackingActivity.newInstance(it.context)
        }

        showCurrentProfile(view.context, Preferences(view.context).currentProfile)
    }

    private fun showCurrentProfile(context: Context, profile: ProfileType) {
        profileName.setText(profile.nameId)
        profileName.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(context, profile.drawableId),
            null,
            ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down_black_24dp),
            null
        )

        profileTag.setBackgroundColor(profile.getColor(context))
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }
}
package ie.justonetech.roadtriptracker.view.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteProfile
import ie.justonetech.roadtriptracker.utils.Preferences
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.view.activities.TrackingActivity
import ie.justonetech.roadtriptracker.view.adapters.ProfileListAdapter
import ie.justonetech.roadtriptracker.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.home_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HomeFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class HomeFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private var profileListAdapter: ProfileListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java).also { model ->
            setupProfileListAdapter(view.context, model)
            setupProfileSelectionButton(view.context, model)
            model.getProfile(Preferences(view.context).currentProfile)
        }

        startTracking.setOnClickListener {
            TrackingActivity.newInstance(it.context)
        }
    }

    private fun setupProfileListAdapter(context: Context, model: ProfileViewModel) {
        model.profileList.observe(viewLifecycleOwner, Observer { profileList ->
            profileListAdapter = ProfileListAdapter(context).apply {
                addAll(profileList)
            }

            profileCardView.isClickable = true
            profileCardView.setOnClickListener {
                profileListAdapter?.let { adapter ->
                    AlertDialog.Builder(it.context).apply {
                        setTitle(R.string.home_fragment_profile_dialog_title)
                        setAdapter(adapter) { _, which ->
                            adapter.getItem(which)?.let {
                                Preferences(context).currentProfile = ProfileType.fromId(it.id)
                                showCurrentProfile(context, it)
                            }
                        }

                        show()
                    }
                }
            }
        })
    }

    private fun setupProfileSelectionButton(context: Context, model: ProfileViewModel) {
        model.profile.observe(viewLifecycleOwner, Observer {
            showCurrentProfile(context, it)
        })
    }

    private fun showCurrentProfile(context: Context, profile: RouteProfile) {
        val profileType = ProfileType.fromId(profile.id)

        profileName.setText(profileType.nameId)
        profileName.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(context, profileType.drawableId),
            null,
            ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down_black_24dp),
            null
        )

        profileTag.setBackgroundColor(profile.tagColor)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }
}
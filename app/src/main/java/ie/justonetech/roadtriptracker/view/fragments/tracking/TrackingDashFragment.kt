package ie.justonetech.roadtriptracker.view.fragments.tracking

import androidx.core.content.ContextCompat
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.TrackingDashFragmentBinding
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.TrackingStats
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.utils.TrackingStatsFormatter
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingDashFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingDashFragment : BaseDashFragment<TrackingDashFragmentBinding>(R.layout.tracking_dash_fragment) {

    override fun onProfileConfigChanged(profileConfig: ProfileConfig) {
        val profileType = ProfileType.fromId(profileConfig.id)

        viewBinding.profileName.setText(profileType.nameId)
        viewBinding.profileName.setCompoundDrawablesWithIntrinsicBounds(
        ContextCompat.getDrawable(viewBinding.root.context, profileType.drawableId),
            null,
            null,
            null
        )

        viewBinding.profileTag.setBackgroundColor(ContextCompat.getColor(viewBinding.root.context, profileType.colorId))

        // DEBUG ONLY

        updateStats(
            TrackingStats(
                Date(),
                0,
                0,

                0.0,

                0f,
                0f,
                0f,
                0f,

                0.0,
                0.0
            )
        )

        // DEBUG ONLY
    }

    override fun updateStats(trackingStats: TrackingStats) {
        viewBinding.trackingStats = TrackingStatsFormatter(viewBinding.root.context, trackingStats)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingDashFragment::class.java.simpleName
    }
}
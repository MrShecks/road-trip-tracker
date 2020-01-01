package ie.justonetech.roadtriptracker.view.fragments.tracking

import androidx.core.content.ContextCompat
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.TrackingDashFragmentBinding
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.TrackingStats
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.utils.SpeedUnit
import ie.justonetech.roadtriptracker.utils.TrackingStatsFormatter

///////////////////////////////////////////////////////////////////////////////////////////////////
// TrackingDashFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class TrackingDashFragment : BaseDashFragment<TrackingDashFragmentBinding>(R.layout.tracking_dash_fragment) {

    private var speedUnit = SpeedUnit.KPH
    private var distanceUnit = DistanceUnit.KILOMETERS

    override fun onProfileConfigChanged(profileConfig: ProfileConfig) {
        val profileType = ProfileType.fromId(profileConfig.id)

        speedUnit = profileConfig.speedUnit
        distanceUnit = profileConfig.distanceUnit

        viewBinding.profileName.setText(profileType.nameId)
        viewBinding.profileName.setCompoundDrawablesWithIntrinsicBounds(
        ContextCompat.getDrawable(viewBinding.root.context, profileType.drawableId),
            null,
            null,
            null
        )

        viewBinding.profileTag.setBackgroundColor(ContextCompat.getColor(viewBinding.root.context, profileType.colorId))
        updateStats(TrackingStats())
    }

    override fun updateStats(trackingStats: TrackingStats) {
        viewBinding.trackingStats = TrackingStatsFormatter(
            viewBinding.root.context,
            trackingStats,
            speedUnit,
            distanceUnit
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = TrackingDashFragment::class.java.simpleName
    }
}
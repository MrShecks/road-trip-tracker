package ie.justonetech.roadtriptracker.view.fragments.tracking

import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.GenericDashFragmentBinding
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.TrackingStats
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.SpeedUnit
import ie.justonetech.roadtriptracker.utils.TrackingStatsFormatter

///////////////////////////////////////////////////////////////////////////////////////////////////
// GenericDashFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class GenericDashFragment : BaseDashFragment<GenericDashFragmentBinding>(R.layout.generic_dash_fragment) {

    private var speedUnit = SpeedUnit.KPH
    private var distanceUnit = DistanceUnit.KILOMETERS

    override fun onProfileConfigChanged(profileConfig: ProfileConfig) {
        speedUnit = profileConfig.speedUnit
        distanceUnit = profileConfig.distanceUnit

        updateStats(TrackingStats.EMPTY)
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
        private val TAG = GenericDashFragment::class.java.simpleName
    }
}
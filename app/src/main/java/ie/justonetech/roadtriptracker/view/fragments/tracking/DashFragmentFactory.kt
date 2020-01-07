package ie.justonetech.roadtriptracker.view.fragments.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.GenericDashFragmentBinding
import ie.justonetech.roadtriptracker.databinding.WalkingDashFragmentBinding
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.TrackingStats
import ie.justonetech.roadtriptracker.utils.DistanceUnit
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.utils.SpeedUnit
import ie.justonetech.roadtriptracker.utils.TrackingStatsFormatter

///////////////////////////////////////////////////////////////////////////////////////////////////
// DashFragmentFactory
///////////////////////////////////////////////////////////////////////////////////////////////////

class DashFragmentFactory {

    interface DashFragment {
        fun setProfile(profileConfig: ProfileConfig)
        fun updateStats(trackingStats: TrackingStats)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // GenericDashFragment
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    class GenericDashFragment : BaseDashFragment<GenericDashFragmentBinding>(R.layout.generic_dash_fragment) {
        override fun updateStats(trackingStats: TrackingStats) {
            viewBinding.trackingStats = TrackingStatsFormatter(
                viewBinding.root.context,
                trackingStats,
                speedUnit,
                distanceUnit
            )
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // WalkingDashFragment
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    class WalkingDashFragment : BaseDashFragment<WalkingDashFragmentBinding>(R.layout.walking_dash_fragment) {
        override fun updateStats(trackingStats: TrackingStats) {
            viewBinding.trackingStats = TrackingStatsFormatter(
                viewBinding.root.context,
                trackingStats,
                speedUnit,
                distanceUnit
            )
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // BaseDashFragment
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    open class BaseDashFragment<VB : ViewDataBinding> protected constructor (@LayoutRes private var layoutId: Int) : Fragment(), DashFragment {

        protected lateinit var viewBinding: VB
        protected var distanceUnit = DistanceUnit.KILOMETERS
        protected var speedUnit = SpeedUnit.KPH

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return DataBindingUtil.inflate<VB>(inflater, layoutId, container, false).also {
                viewBinding = it

            }.root
        }

        override fun setProfile(profileConfig: ProfileConfig) {
            distanceUnit = profileConfig.distanceUnit
            speedUnit = profileConfig.speedUnit

            updateStats(TrackingStats.EMPTY)
            onProfileConfigChanged(profileConfig)
        }

        override fun updateStats(trackingStats: TrackingStats) {
            // Implemented by sub classes to handle updating stats
        }

        protected open fun onProfileConfigChanged(profileConfig: ProfileConfig) {
            // Implemented by sub classes to handle profile configuration load/change
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = DashFragmentFactory::class.java.simpleName

        @JvmStatic
        fun newInstance(profileType: ProfileType): BaseDashFragment<*> {
            return when(profileType) {
                ProfileType.PROFILE_TYPE_WALKING,
                ProfileType.PROFILE_TYPE_RUNNING -> WalkingDashFragment()

                else -> GenericDashFragment()
            }
        }
    }
}
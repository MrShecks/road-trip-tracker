package ie.justonetech.roadtriptracker.view.fragments.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ie.justonetech.roadtriptracker.model.ProfileConfig
import ie.justonetech.roadtriptracker.model.TrackingStats
import ie.justonetech.roadtriptracker.utils.ProfileType
import ie.justonetech.roadtriptracker.viewmodel.ProfileViewModel

///////////////////////////////////////////////////////////////////////////////////////////////////
// BaseDashFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

open class BaseDashFragment<VB : ViewDataBinding>(@LayoutRes private var layoutId: Int) : Fragment() {

    protected lateinit var viewBinding: VB
    protected lateinit var profileConfig: ProfileConfig

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<VB>(inflater, layoutId, container, false).also {
            ViewModelProviders.of(this).get(ProfileViewModel::class.java).also { model ->
                model.profile.observe(viewLifecycleOwner, Observer { profileConfig ->
                    onProfileConfigChanged(profileConfig)
                })
            }

            viewBinding = it
        }.root
    }

    fun setProfile(profileConfig: ProfileConfig) {
        onProfileConfigChanged(profileConfig)
    }

    open fun updateStats(trackingStats: TrackingStats) {
        // Implemented by sub classes to handle updating stats
    }

    protected open fun onProfileConfigChanged(profileConfig: ProfileConfig) {
        // Implemented by sub classes to handle profile configuration load/change
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = BaseDashFragment::class.java.simpleName
    }
}
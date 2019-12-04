package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.adapters.RouteHistoryListAdapter
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.history_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HistoryFragment
// Shows a historical list of all tracked routes currently in the database
///////////////////////////////////////////////////////////////////////////////////////////////////

class HistoryFragment : Fragment() {

    private lateinit var viewModel: RouteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java).also { model ->
            routeSummaryList.adapter = RouteHistoryListAdapter()

            model.routeList.observe(this, Observer {
                Log.d(TAG, "Adding route list page to recycler: Size=${it.size}")

                (routeSummaryList.adapter as RouteHistoryListAdapter).submitList(it)
            })
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = HistoryFragment::class.java.simpleName
    }
}
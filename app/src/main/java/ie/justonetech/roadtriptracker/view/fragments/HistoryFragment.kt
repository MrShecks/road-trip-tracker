package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.view.widgets.RouteSummaryRecyclerView
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.history_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HistoryFragment
// Shows a historical list of all tracked routes currently in the database
///////////////////////////////////////////////////////////////////////////////////////////////////

class HistoryFragment : Fragment(), ActionMode.Callback {

    private lateinit var viewModel: RouteViewModel
    private var actionMode: ActionMode? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java).also { model ->

            routeSummaryList.setOnItemClickListener(object: RouteSummaryRecyclerView.ItemClickListener {
                override fun onItemClicked(item: RouteSummary) {
                    HistoryFragmentDirections.actionDestinationHistoryToRouteDetail(item.id!!).also { action ->
                        Navigation.findNavController(routeSummaryList).navigate(action)
                    }
                }

                override fun onItemLongClicked(item: RouteSummary) {
                }

            })

            routeSummaryList.setOnItemSelectionListener(object: RouteSummaryRecyclerView.ItemSelectionChangedListener{
                override fun onBeginMultiSelect() {
                    actionMode = startActionMode(this@HistoryFragment)
                }

                override fun onEndMultiSelect() {
                    Log.i(TAG, "onEndMultiSelect()")
                }

                override fun onItemSelectionChanged(selectedItems: Set<RouteSummary>) {
                    actionMode?.let {
                        it.title = resources.getQuantityString(R.plurals.route_list_selection_action_title, selectedItems.size, selectedItems.size)
                    }
                }
            })

            model.routeList.observe(this, Observer {
                routeSummaryList.submitList(it)
            })
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun startActionMode(callback: ActionMode.Callback): ActionMode? {
        return activity?.startActionMode(callback)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ActionMode.Callback
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        routeSummaryList.cancelMultiSelectMode()
        actionMode?.finish()

        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.route_history_action_menu, menu)

        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        // Not Used
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = HistoryFragment::class.java.simpleName
    }
}
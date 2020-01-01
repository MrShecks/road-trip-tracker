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
            })

            routeSummaryList.setOnItemSelectionListener(object: RouteSummaryRecyclerView.ItemSelectionChangedListener {
                override fun onBeginMultiSelect() {
                    check(actionMode == null) { "|actionMode| should be null when onBeginMultiSelect() is called" }

                    actionMode = startActionMode(this@HistoryFragment)
                }

                override fun onEndMultiSelect() {
                    Log.i(TAG, "onEndMultiSelect() Called")
                    check(actionMode != null) { "|actionMode| should not be null when onEndMultiSelect() is called"}

                    actionMode?.finish()
                    actionMode = null
                }

                override fun onItemSelectionChanged(selectedItemCount: Int) {
                    check(actionMode != null) { "|actionMode| should not be null when onItemSelectionChanged() is called"}

                    actionMode?.let {
                        it.title = resources.getQuantityString(R.plurals.route_list_selection_action_title, selectedItemCount, selectedItemCount)
                    }
                }
            })

            model.routeList.observe(viewLifecycleOwner, Observer {
                routeSummaryList.submitList(it)
            })
        }

        //
        // Note: In order for selection tracking to persisted between device configuration
        // changes we need include the RouteSummaryRecyclerView in the fragment lifecycle
        // bu calling onRestoreInstanceState() and onSaveInstanceState() in response
        // the the corresponding Fragment lifecycle calls.
        //
        // Note: onRestoreInstanceState() should be called AFTER calling
        // RouteSummaryRecyclerView.setOnItemSelectionListener() above so that we don't
        // miss notifications when the selection state is restored.
        //

        routeSummaryList.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // FIXME: For some reason onSaveInstanceState() is called when
        // FIXME: the RouteDetailFragment() displayed and there is a device
        // FIXME: configuration change, when this happens routeSummaryList
        // FIXME: is null and the application crashes. The ?. safe call is a hack!

        // Save any RecyclerView selections
        routeSummaryList?.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()

        actionMode?.finish()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun startActionMode(callback: ActionMode.Callback): ActionMode? {
        return activity?.startActionMode(callback)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ActionMode.Callback
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        routeSummaryList.getSelectedIds().forEach {
            Log.i(TAG, "Selected RouteId=$it")
        }

        routeSummaryList.endMultiSelect()

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
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = HistoryFragment::class.java.simpleName
    }
}


/*
 * This file is part of Road Trip Tracker.
 *
 * Road Trip Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Road Trip Tracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Road Trip Tracker.  If not, see <https://www.gnu.org/licenses/>.
 */

package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.model.TrackingRepository
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

        viewModel = ViewModelProvider(this).get(RouteViewModel::class.java).also { model ->

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

        // FIXME: I don't think this is working anymore, I must have broken something

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
        return requireActivity().startActionMode(callback)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ActionMode.Callback
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        item?.let {
            val selectedIds = routeSummaryList.getSelectedIds()

            when(it.itemId) {
                R.id.action_delete -> onDeleteSelected(selectedIds)
                else -> {
                    // TODO: Add other options here...
                }
            }

            routeSummaryList.endMultiSelect()
        }

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
        routeSummaryList.endMultiSelect()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun onDeleteSelected(selectedIds: List<Int>) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.route_history_delete_prompt_title)
            setMessage(getString(R.string.route_history_delete_selected_prompt_message, selectedIds.size))

            setPositiveButton(android.R.string.yes) { _, _ ->
                TrackingRepository(context).deleteRoutes(selectedIds)
            }

            setNegativeButton(android.R.string.no) { _, _ ->
                // Nothing to do here...
            }

            create().show()
        }
    }

    companion object {
        private val TAG = HistoryFragment::class.java.simpleName
    }
}


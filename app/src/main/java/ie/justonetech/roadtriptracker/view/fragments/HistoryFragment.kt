package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.view.adapters.RouteHistoryListAdapter
import ie.justonetech.roadtriptracker.view.utils.RecyclerItemClickListener
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.history_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// HistoryFragment
// Shows a historical list of all tracked routes currently in the database
///////////////////////////////////////////////////////////////////////////////////////////////////

class HistoryFragment : Fragment(), ActionMode.Callback {

    private lateinit var viewModel: RouteViewModel
    private var actionMode: ActionMode? = null

    private var selected: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java).also { model ->
            routeSummaryList.adapter = RouteHistoryListAdapter()

            context?.let {
                routeSummaryList.addOnItemTouchListener(
                    RecyclerItemClickListener(it, routeSummaryList).apply {
                        setItemClickListener(object: RecyclerItemClickListener.ItemClickListener {
                            override fun onItemClicked(position: Int, view: View) {

                                // FIXME: This is all test code, need to clean up item selection

                                if(actionMode != null) {

                                    (view as MaterialCardView).also { card ->
                                        selected = if(card.isChecked) selected - 1 else selected + 1
                                        card.isChecked = !card.isChecked
                                    }

                                    actionMode?.title = "$selected Selected"

                                } else {
                                    Snackbar.make(getView()!!, "Item=$position, Clicked", Snackbar.LENGTH_LONG).show()
                                }

                            }
                        })

                        setItemLongClickListener(object: RecyclerItemClickListener.ItemLongClickListener {
                            override fun onItemLongClicked(position: Int, view: View) {

                                // FIXME: This is all test code, need to clean up item selection

                                if(actionMode == null) {
                                    selected++
                                    actionMode = startActionMode(this@HistoryFragment)?.apply {
                                        title = "$selected Selected"
                                    }
                                }

                                (view as MaterialCardView).also { card ->
                                    card.isChecked = !card.isChecked
                                }
                            }
                        })
                    }
                )
            }

            model.routeList.observe(this, Observer {
                (routeSummaryList.adapter as RouteHistoryListAdapter).submitList(it)
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
        return false
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
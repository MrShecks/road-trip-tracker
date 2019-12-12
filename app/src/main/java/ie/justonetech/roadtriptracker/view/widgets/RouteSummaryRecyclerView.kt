package ie.justonetech.roadtriptracker.view.widgets

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.paging.PagedList
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.view.adapters.RouteSummaryListAdapter

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummaryRecyclerView
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteSummaryRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    interface ItemClickListener {
        fun onItemClicked(item: RouteSummary)
    }

    interface ItemSelectionChangedListener {
        fun onBeginMultiSelect()
        fun onEndMultiSelect()

        fun onItemSelectionChanged(selectedItemCount: Int)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private class RouteSummaryItemDetailsLookup(private val recyclerView: RouteSummaryRecyclerView) : ItemDetailsLookup<Long>() {

        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            return recyclerView.findChildViewUnder(event.x, event.y)?.let {
                (recyclerView.getChildViewHolder(it) as RouteSummaryListAdapter.RouteSummaryViewHolder).getItemDetails()
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private var itemClickListener: ItemClickListener? = null
    private var itemSelectionChangedListener: ItemSelectionChangedListener? = null

    private val itemSelectionTracker: SelectionTracker<Long>
    private var isMultiSelectActive = false

    init {
        setHasFixedSize(true)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)

        //
        // Note: The following call sequence is important. The Recycler.Selection library
        // has a bit of a circular dependency on the SelectionTracker. In order to handle
        // item selection correctly the Adaptor needs access to the SelectionTracker, however
        // SelectionTracker.Builder checks to make sure that the RecyclerView passed already
        // has a non-null adapter set. So we need to assign the RecyclerView adapter first,
        // build the SelectionTracker and then assign the SelectionTracker to the Adapter.
        //
        // Well done Google ;)
        //

        adapter = RouteSummaryListAdapter()

        itemSelectionTracker = SelectionTracker.Builder<Long>(
            SELECTION_TRACKING_ID,
            this,
            StableIdKeyProvider(this),
            RouteSummaryItemDetailsLookup(this),
            StorageStrategy.createLongStorage()

        )
        .withSelectionPredicate(SelectionPredicates.createSelectAnything())
        .withOnItemActivatedListener { item, _ ->
            getItem(item.position)?.let {
                itemClickListener?.onItemClicked(it)
            }

            true
        }
        .build()

        (adapter as RouteSummaryListAdapter).selectionTracker = itemSelectionTracker

        itemSelectionTracker.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {

                //
                // If this is the first selection change event then notify the listener
                // to let them know that multi selection mode has begun
                //

                if(!isMultiSelectActive) {
                    itemSelectionChangedListener?.onBeginMultiSelect()
                    isMultiSelectActive = true
                }

                //
                // Notify the listener with the updated selection count
                //

                itemSelectionChangedListener?.onItemSelectionChanged(itemSelectionTracker.selection.size())
            }

            override fun onSelectionRestored() {

                //
                // Called when the selection state is restored after onRestoreInstanceState()
                // This happens in response to configure changes (device rotations) so we
                // need to notify any listeners so that any selection UI (ActionMode menu)
                // can be restored correctly.
                //
                // Note: We also call onItemSelectionChanged() with the selected item count
                //

                if(!itemSelectionTracker.selection.isEmpty) {
                    itemSelectionChangedListener?.let {
                        it.onBeginMultiSelect()
                        it.onItemSelectionChanged(itemSelectionTracker.selection.size())
                    }

                    isMultiSelectActive = true
                }
            }
        })

    }

    fun submitList(pagedList: PagedList<RouteSummary>) {
        (adapter as RouteSummaryListAdapter).submitList(pagedList)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setOnItemSelectionListener(itemSelectionChangedListener: ItemSelectionChangedListener) {
        this.itemSelectionChangedListener = itemSelectionChangedListener
    }

    fun onRestoreInstanceState(state: Bundle?) {
        itemSelectionTracker.onRestoreInstanceState(state)
    }

    fun onSaveInstanceState(outState: Bundle) {
        itemSelectionTracker.onSaveInstanceState(outState)
    }

    fun endMultiSelect() {
        if(isMultiSelectActive) {
            itemSelectionTracker.clearSelection()
            itemSelectionChangedListener?.onEndMultiSelect()
            isMultiSelectActive = false
        }
    }

    fun getSelection(): List<RouteSummary> {
        return itemSelectionTracker.selection.mapNotNull { selectedId ->
            selectedId?.let {
                getItem(it.toInt())
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getItem(position: Int): RouteSummary? {
        return (adapter as RouteSummaryListAdapter).getItem(position)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteSummaryRecyclerView::class.java.simpleName

        private const val SELECTION_TRACKING_ID: String = "_route_summary_selection_id"
    }
}
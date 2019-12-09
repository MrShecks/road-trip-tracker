package ie.justonetech.roadtriptracker.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.view.adapters.RouteSummaryListAdapter
import ie.justonetech.roadtriptracker.view.utils.RecyclerItemClickListener

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummaryRecyclerView
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteSummaryRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    enum class SelectionMode {
        SINGLE_SELECT,
        MULTI_SELECT
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    interface ItemClickListener {
        fun onItemClicked(item: RouteSummary)
        fun onItemLongClicked(item: RouteSummary)
    }

    interface ItemSelectionChangedListener {
        fun onBeginMultiSelect()
        fun onEndMultiSelect()

        fun onItemSelectionChanged(selectedItems: Set<RouteSummary>)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    var selectionMode = SelectionMode.SINGLE_SELECT
        private set

    val selectedItems = mutableSetOf<RouteSummary>()

    private var itemClickListener: ItemClickListener? = null
    private var itemSelectionChangedListener: ItemSelectionChangedListener? = null

    private val selectedItemIds = mutableSetOf<Int>()

    init {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        adapter = RouteSummaryListAdapter()

        addOnItemTouchListener(RecyclerItemClickListener(context, this).apply {
            setItemClickListener(object: RecyclerItemClickListener.ItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    getItem(position)?.also { item ->
                        when(selectionMode) {
                            SelectionMode.SINGLE_SELECT -> {
                                itemClickListener?.onItemClicked(item)
                            }

                            SelectionMode.MULTI_SELECT -> {
                                if(view is Checkable) {
                                    toggleItemSelection(position, view, item)
                                    itemSelectionChangedListener?.onItemSelectionChanged(selectedItems)
                                }
                            }
                        }
                    }
                }
            })

            setItemLongClickListener(object: RecyclerItemClickListener.ItemLongClickListener {
                override fun onItemLongClicked(position: Int, view: View) {
                    getItem(position)?.let { item ->
                        when(selectionMode) {
                            SelectionMode.SINGLE_SELECT -> {
                                if(view is Checkable && !view.isChecked) {
                                    selectionMode = SelectionMode.MULTI_SELECT
                                    toggleItemSelection(position, view, item)

                                    itemSelectionChangedListener?.let {
                                        it.onBeginMultiSelect()
                                        it.onItemSelectionChanged(selectedItems)
                                    }
                                }
                            }

                            SelectionMode.MULTI_SELECT -> {

                            }
                        }
                    }
                }
            })
        })
    }

    fun submitList(pagedList: PagedList<RouteSummary>) {
        (adapter as RouteSummaryListAdapter).submitList(pagedList)
    }

    fun cancelMultiSelectMode(clearSelection: Boolean = true) {

        if(clearSelection) {

            //
            // TODO: Figure out I should uncheck the list items
            //

//            selectedItemIds.forEach { id ->
//
//            }

            selectedItems.clear()
            selectedItemIds.clear()
        }

        selectionMode = SelectionMode.SINGLE_SELECT
        itemSelectionChangedListener?.onEndMultiSelect()
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setOnItemSelectionListener(itemSelectionListener: ItemSelectionChangedListener) {
        this.itemSelectionChangedListener = itemSelectionListener
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getItem(position: Int): RouteSummary? {
        return (adapter as RouteSummaryListAdapter).getItem(position)
    }

    private fun toggleItemSelection(position: Int, view: Checkable, item: RouteSummary) {
        if(view.isChecked) {
            selectedItems.remove(item)
            selectedItemIds.remove(position)
            view.isChecked = false
        } else {
            selectedItems.add(item)
            selectedItemIds.add(position)
            view.isChecked = true
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteSummaryRecyclerView::class.java.simpleName
    }
}
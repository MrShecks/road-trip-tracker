package ie.justonetech.roadtriptracker.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

///////////////////////////////////////////////////////////////////////////////////////////////////
// PagingListAdapter
// Implementation of a generic PagedListAdapter with additional support list item click handling
//
// Subclasses should implement the PagingListAdapter.createViewHolder() abstract method to return
// view holder instances. A view holder should be subclass of PagedListAdapter.ViewHolder or
// PagingListAdapter.BindingViewHolder if view raw binding support is required.
///////////////////////////////////////////////////////////////////////////////////////////////////

abstract class PagingListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>)
    : PagedListAdapter<T, PagingListAdapter.ViewHolder<T>>(diffCallback) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    interface ItemClickListener<T> {
        fun onItemClicked(position: Int, item: T)
    }

    interface ItemLongClickListener<T> {
        fun onItemLongClicked(position: Int, item: T)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ViewHolder
    // Base class for all view holders maintained by the list adapter
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    abstract class ViewHolder<T>(protected val adapter: PagingListAdapter<T>, itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        constructor(adapter: PagingListAdapter<T>, parent: ViewGroup, @LayoutRes layoutId: Int, attachToRoot: Boolean = false)
                : this(adapter, LayoutInflater.from(parent.context).inflate(layoutId, parent, attachToRoot))

        protected fun onItemClicked() {

            adapter.getItem(adapterPosition)?.let {
                adapter.itemClickListener?.onItemClicked(adapterPosition, it)
            }
        }

        protected fun onItemLongClicked() {
            adapter.getItem(adapterPosition)?.let {
                adapter.itemLongClickListener?.onItemLongClicked(adapterPosition, it)
            }
        }

        abstract fun onBindView(item: T)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // BindingViewHolder
    // Base class for view holder with additional support for view raw binding
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    abstract class BindingViewHolder<T, VB : ViewDataBinding>(adapter: PagingListAdapter<T>, parent: ViewGroup, @LayoutRes layoutId: Int, attachToRoot: Boolean = false)
        : ViewHolder<T>(adapter, DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), layoutId, parent, attachToRoot).root) {

        protected val viewBinding = DataBindingUtil.getBinding<VB>(itemView)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private var itemClickListener: ItemClickListener<T>? = null
    private var itemLongClickListener: ItemLongClickListener<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return createViewHolder(this, parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<T>, position: Int) {
        getItem(position)?.let {
            viewHolder.onBindView(it)
        }
    }

    public override fun getItem(position: Int): T? {
        return super.getItem(position)
    }

    fun setOnItemClickListener(listener: ItemClickListener<T>) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: ItemLongClickListener<T>) {
        itemLongClickListener = listener
    }

    protected abstract fun createViewHolder(adapter: PagingListAdapter<T>, parent: ViewGroup, viewType: Int): ViewHolder<T>
}
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

package ie.justonetech.roadtriptracker.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

///////////////////////////////////////////////////////////////////////////////////////////////////
// PagingListAdapter
// Implementation of a generic PagedListAdapter with additional support for item selection
//
// Subclasses should implement the PagingListAdapter.createViewHolder() abstract method to return
// view holder instances. A view holder should be subclass of PagedListAdapter.ViewHolder or
// PagingListAdapter.BindingViewHolder if view raw binding support is required.
///////////////////////////////////////////////////////////////////////////////////////////////////

abstract class PagingListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>)
    : PagedListAdapter<T, PagingListAdapter.ViewHolder<T>>(diffCallback) {

    var selectionTracker: SelectionTracker<Long>? = null

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ViewHolder
    // Base class for all view holders maintained by the list adapter
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    abstract class ViewHolder<T>(protected val adapter: PagingListAdapter<T>, itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        constructor(adapter: PagingListAdapter<T>, parent: ViewGroup, @LayoutRes layoutId: Int, attachToRoot: Boolean = false)
                : this(adapter, LayoutInflater.from(parent.context).inflate(layoutId, parent, attachToRoot))

        abstract fun onBindView(item: T)

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object: ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? = adapter.getItemId(adapterPosition)
                override fun getPosition(): Int = adapterPosition
            }
        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return createViewHolder(this, parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<T>, position: Int) {
        getItem(position)?.let { item ->

            selectionTracker?.let {
                viewHolder.itemView.isActivated = it.isSelected(getItemId(position))
            }

            viewHolder.onBindView(item)
        }
    }

    public override fun getItem(position: Int): T? {
        return super.getItem(position)
    }

    protected abstract fun createViewHolder(adapter: PagingListAdapter<T>, parent: ViewGroup, viewType: Int): ViewHolder<T>
}
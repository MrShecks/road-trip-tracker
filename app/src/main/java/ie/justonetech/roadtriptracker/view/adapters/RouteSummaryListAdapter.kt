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

import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.DiffUtil
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.RouteSummaryListItemBinding
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.utils.RouteSummaryFormatter

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteSummaryListAdapter
// Paged list adapter used to show the route summary history list
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteSummaryListAdapter : PagingListAdapter<RouteSummary>(DIFF_CALLBACK) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // RouteSummaryViewHolder
    // View holder for a list item summary information for a route
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    class RouteSummaryViewHolder(adapter: PagingListAdapter<RouteSummary>, parent: ViewGroup)
        : PagingListAdapter.BindingViewHolder<RouteSummary, RouteSummaryListItemBinding>(adapter, parent, R.layout.route_summary_list_item) {

        override fun onBindView(item: RouteSummary) {
            viewBinding?.let { binding ->
                binding.cardView.isChecked = itemView.isActivated
                binding.routeSummary = RouteSummaryFormatter(binding.root.context, item)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        setHasStableIds(true)
    }

    override fun createViewHolder(adapter: PagingListAdapter<RouteSummary>, parent: ViewGroup, viewType: Int): ViewHolder<RouteSummary> {
        return RouteSummaryViewHolder(adapter, parent)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id?.toLong()
            ?: super.getItemId(position)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteSummaryListAdapter::class.java.simpleName

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RouteSummary>() {
            override fun areItemsTheSame(first: RouteSummary, second: RouteSummary): Boolean {
                return first.id == second.id
            }

            override fun areContentsTheSame(first: RouteSummary, second: RouteSummary): Boolean {
                return first == second
            }
        }
    }
}
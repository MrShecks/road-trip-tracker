package ie.justonetech.roadtriptracker.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.databinding.RouteSummaryListItemBinding
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.utils.RouteSummaryFormatter

///////////////////////////////////////////////////////////////////////////////////////////////////
// RouteHistoryListAdapter
// Paged list adapter used to show the route summary history list
///////////////////////////////////////////////////////////////////////////////////////////////////

class RouteHistoryListAdapter : PagingListAdapter<RouteSummary>(DIFF_CALLBACK) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // RouteSummaryViewHolder
    // View holder for a list item summary information for a route
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private class RouteSummaryViewHolder(adapter: PagingListAdapter<RouteSummary>, parent: ViewGroup)
        : PagingListAdapter.BindingViewHolder<RouteSummary, RouteSummaryListItemBinding>(adapter, parent, R.layout.route_summary_list_item) {

        override fun onBindView(item: RouteSummary) {
            viewBinding?.let { binding ->
                binding.routeSummary = RouteSummaryFormatter(binding.root.context, item)

                binding.root.setOnClickListener{
                    onItemClicked()
                }
            }
        }
    }

    override fun createViewHolder(adapter: PagingListAdapter<RouteSummary>, parent: ViewGroup, viewType: Int): ViewHolder<RouteSummary> {
        return RouteSummaryViewHolder(adapter, parent)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = RouteHistoryListAdapter::class.java.simpleName

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
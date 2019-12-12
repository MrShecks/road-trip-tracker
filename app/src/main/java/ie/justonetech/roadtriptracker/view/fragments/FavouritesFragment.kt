package ie.justonetech.roadtriptracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteSummary
import ie.justonetech.roadtriptracker.view.widgets.RouteSummaryRecyclerView
import ie.justonetech.roadtriptracker.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.favourites_fragment.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// FavouritesFragment
///////////////////////////////////////////////////////////////////////////////////////////////////

class FavouritesFragment : Fragment() {

    private lateinit var viewModel: RouteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.favourites_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java).also { model ->

            routeFavouriteList.setOnItemClickListener(object: RouteSummaryRecyclerView.ItemClickListener {
                override fun onItemClicked(item: RouteSummary) {
                    FavouritesFragmentDirections.actionDestinationFavouritesToRouteDetail(item.id!!).also { action ->
                        Navigation.findNavController(routeFavouriteList).navigate(action)
                    }
                }
            })

            model.favouriteRouteList.observe(this, Observer {
                routeFavouriteList.submitList(it)
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

        routeFavouriteList.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // FIXME: For some reason onSaveInstanceState() is called when
        // FIXME: the RouteDetailFragment() displayed and there is a device
        // FIXME: configuration change, when this happens routeSummaryList
        // FIXME: is null and the application crashes. The ?. safe call is a hack!

        // Save any RecyclerView selections
        routeFavouriteList?.onSaveInstanceState(outState)
    }
}
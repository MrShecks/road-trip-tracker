package ie.justonetech.roadtriptracker.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ie.justonetech.roadtriptracker.R
import ie.justonetech.roadtriptracker.model.RouteDetail
import ie.justonetech.roadtriptracker.model.TrackingRepository
import ie.justonetech.roadtriptracker.model.db.TrackingDatabase
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// MainActivity
// Application entry point, hosts the main navigation view
///////////////////////////////////////////////////////////////////////////////////////////////////

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        navHostFragment.findNavController().also { navController ->
            AppBarConfiguration.Builder(navController.graph).build().also {
                setSupportActionBar(toolBar)
                setupActionBarWithNavController(navController, it)
            }

            bottomNavigationView.setupWithNavController(navController)
        }

//        for(i in 1..15) {
//            TrackingRepository(this).addRoute(
//                RouteDetail(
//                    null,
//                    Date(),
//                    Date(),
//                    0,
//                    0,
//                    0.0,
//                    0f,
//                    0f,
//                    0.0,
//                    emptyList()
//                )
//            )
//        }

        TrackingRepository(this).getRouteList().observe(this, androidx.lifecycle.Observer {

            Log.d(TAG, "Route list received from database: PageSize=${it.size}")
        })
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
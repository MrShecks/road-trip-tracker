package ie.justonetech.roadtriptracker.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ie.justonetech.roadtriptracker.R
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_toolbar.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// MainActivity
// Application entry point, hosts the main navigation view
///////////////////////////////////////////////////////////////////////////////////////////////////

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolBar)

        toolBar.setTitle("Hello")

        Navigation.findNavController(this, R.id.navHostFragment).also { navController ->
            appBarConfiguration = AppBarConfiguration(setOf(R.id.destination_home, R.id.destination_history, R.id.destination_settings), drawerLayout)

            setupActionBar(navController)
            setupBottomNavigationMenu(navController)
            setupSideNavigationMenu(navController)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.navHostFragment), appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setupActionBar(navController: NavController) {
        //collapsingToolbarLayout.setupWithNavController(toolBar, navController, appBarConfiguration)

        setupActionBarWithNavController(navController, appBarConfiguration)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }

    private fun setupBottomNavigationMenu(navController: NavController) {
        bottomNavigationView?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupSideNavigationMenu(navController: NavController) {
        sideNavigationView?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
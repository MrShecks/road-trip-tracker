package ie.justonetech.roadtriptracker.view.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
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

        Navigation.findNavController(this, R.id.navHostFragment).also { navController ->
            appBarConfiguration = AppBarConfiguration(setOf(R.id.destination_home, R.id.destination_history, R.id.destination_favourites), drawerLayout)

            setupActionBar(navController)
            setupBottomNavigationMenu(navController)
            setupSideNavigationMenu(navController)
            setupNavigationChangeListener(navController)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.navHostFragment), appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return Navigation.findNavController(this, R.id.navHostFragment).run {
            NavigationUI.onNavDestinationSelected(item!!, this)

        } || super.onOptionsItemSelected(item)
    }

    private fun setupActionBar(navController: NavController) {
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

    private fun setupNavigationChangeListener(navController: NavController) {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            // TODO: Hide/Remove the settings toolbar action when the settings fragment is visible

            Log.i(TAG, "OnDestinationChangedListener() destination=$destination")
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
package ie.justonetech.roadtriptracker.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ie.justonetech.roadtriptracker.R
import kotlinx.android.synthetic.main.main_activity.*

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
    }
}
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

package ie.justonetech.roadtriptracker.view.utils

import android.content.Context
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

///////////////////////////////////////////////////////////////////////////////////////////////////
// AppCompatActivityExt
// Extension utility functions for AppCompatActivity
///////////////////////////////////////////////////////////////////////////////////////////////////

fun AppCompatActivity.addFragment(@IdRes containerId: Int, fragment: Fragment) {
    with(supportFragmentManager.beginTransaction()) {
        add(containerId, fragment)
        commit()
    }
}

fun AppCompatActivity.replaceFragment(@IdRes containerId: Int, fragment: Fragment) {
    with(supportFragmentManager.beginTransaction()) {
        replace(containerId, fragment)
        commit()
    }
}

inline fun <reified T> AppCompatActivity.findFragmentById(@IdRes containerId: Int): T? {
    return supportFragmentManager.findFragmentById(containerId) as T?
}

fun AppCompatActivity.getRotation(): Int {
    return (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
}
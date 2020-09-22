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

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

///////////////////////////////////////////////////////////////////////////////////////////////////
// ViewPagerAdapter
///////////////////////////////////////////////////////////////////////////////////////////////////

class ViewPagerAdapter<T: Fragment>(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    data class Page<T>(val title: String, var fragment: T) {
        // TODO: Add support for icons
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private val pages: ArrayList<Page<T>> = ArrayList()

    override fun getPageTitle(position: Int): CharSequence = pages[position].title
    override fun getItem(position: Int): T = pages[position].fragment
    override fun getCount(): Int = pages.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        //
        // HACK: The FragmentPageAdapter caches and manages fragments using the FragmentManager
        // so during configuration changes (e.g screen rotations) it may re-create and initialise
        // our fragments. If we need to access the fragments in the adapter we need to keep
        // the pages list up to date with these new instances
        //
        // http://www.t3hmun.com/posts/2017-01-24_Fragment-Life-Cycle-in-a-ViewPager.html
        //

        return super.instantiateItem(container, position).also {
            @Suppress("UNCHECKED_CAST")
            pages[position].fragment = it as T
        }
    }

    fun addPage(context: Context, @StringRes titleId: Int, fragment: T) {
        addPage(Page(context.getString(titleId), fragment))
    }

    fun addPage(page: Page<T>) = pages.add(page)

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG: String = ViewPagerAdapter::class.java.simpleName
    }
}
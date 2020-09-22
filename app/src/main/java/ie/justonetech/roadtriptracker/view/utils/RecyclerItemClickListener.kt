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
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

///////////////////////////////////////////////////////////////////////////////////////////////////
// RecyclerItemClickListener
// Utility class to translate touch events into item click event for a RecyclerView
///////////////////////////////////////////////////////////////////////////////////////////////////

class RecyclerItemClickListener(context: Context, private val recyclerView: RecyclerView)
    : RecyclerView.OnItemTouchListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    interface ItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    interface ItemLongClickListener {
        fun onItemLongClicked(position: Int, view: View)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setItemLongClickListener(itemLongClickListener: ItemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener
    }

    override fun onInterceptTouchEvent(view: RecyclerView, event: MotionEvent): Boolean {
        view.findChildViewUnder(event.x, event.y)?.let { childView ->
            if(gestureDetector.onTouchEvent(event))
                itemClickListener?.onItemClicked(view.getChildAdapterPosition(childView), childView)
        }

        return false
    }

    override fun onTouchEvent(view: RecyclerView, event: MotionEvent) {
        // Unused
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // Unused
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private var itemClickListener: ItemClickListener? = null
    private var itemLongClickListener: ItemLongClickListener? = null

    private val gestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(event: MotionEvent?) = true

        override fun onLongPress(event: MotionEvent?) {
            event?.let {
                recyclerView.findChildViewUnder(it.x, it.y)?.let { childView ->
                    itemLongClickListener?.onItemLongClicked(recyclerView.getChildAdapterPosition(childView), childView)
                }
            }
        }
    })

}
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

class RecyclerItemClickListener(context: Context, private val recyclerView: RecyclerView) : RecyclerView.OnItemTouchListener {

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
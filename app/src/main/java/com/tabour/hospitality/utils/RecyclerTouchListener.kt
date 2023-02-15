package com.tabour.hospitality.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerTouchListener(
    context: Context,
    recyclerView: RecyclerView,
    clickListener: ClickListener
) : RecyclerView.OnItemTouchListener {

    private val gestureDetector: GestureDetector? = null
    private val clickListener: ClickListener? = null


    override fun onInterceptTouchEvent(rv: RecyclerView, me: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(me.getX(), me.getY())
        if (child != null && clickListener != null && gestureDetector!!.onTouchEvent(me)) {
            clickListener.onClick(child, rv.getChildPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("Not yet implemented")
    }

    interface ClickListener {
        fun onClick(view: View?, position: Int)
        fun onLongClick(view: View?, position: Int)
    }

}
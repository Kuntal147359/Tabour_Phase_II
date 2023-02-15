package com.tabour.hospitality.utils

import android.content.Context
import android.util.TypedValue

class Tools {
    companion object {
        fun dpToPx(context: Context, i: Int): Int {
            return Math.round(
                TypedValue.applyDimension(
                    1,
                    i.toFloat(), context.resources.displayMetrics
                )
            )
        }
    }
}
package com.tabour.hospitality.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat


class BackdropViewAnimation {
    private val TAG = "BackdropViewAnimation"
    private val animatorSet = AnimatorSet()
    private var backdrop: View? = null
    private var backdropShown = false
    private var buttonView: View? = null
    private var closeIcon: Int? = null
    private var colorIcon: Int? = null
    private var context: Context? = null
    private var displayMetrics: DisplayMetrics? = null
    private var height = 0
    private val interpolator: Interpolator = AccelerateDecelerateInterpolator()
    private var openIcon: Int? = null
    private var sheet: View? = null
    private var stateListener: StateListener? = null

    interface StateListener {
        fun onClose(objectAnimator: ObjectAnimator?)

        fun onOpen(objectAnimator: ObjectAnimator?)
    }

    fun addStateListener(stateListener2: StateListener?) {
        stateListener = stateListener2
    }

    constructor(context2: Context, view: View?, view2: View?): super() {
        context = context2
        backdrop = view
        sheet = view2
        displayMetrics = DisplayMetrics()
        (context2 as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics!!.heightPixels
    }

    constructor(
        context2: Context,
        view: View?,
        view2: View?,
        num: Int?,
        num2: Int?,
        num3: Int?
    ) : super() {
        context = context2
        backdrop = view
        sheet = view2
        openIcon = num
        closeIcon = num2
        colorIcon = num3
        displayMetrics = DisplayMetrics()
        (context2 as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics!!.heightPixels
    }

    fun toggle(): ObjectAnimator? {
        return toggle(null as View?)
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun toggle(view: View?): ObjectAnimator? {
        backdropShown = !backdropShown
        if (view != null) {
            buttonView = view
        }
        val view2 = buttonView
        view2?.let { updateIcon(it) }
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()
        var bottom = backdrop!!.bottom - sheet!!.top
        if (backdrop!!.bottom + sheet!!.top > height && getActionBarSize() > 0) {
            bottom = height - sheet!!.top - getActionBarSize() * 4 / 3
        }
        val view3 = sheet
        val fArr = FloatArray(1)
        fArr[0] = if (backdropShown) bottom.toFloat() else 0.0f
        val ofFloat = ObjectAnimator.ofFloat(view3, "translationY", *fArr)
        ofFloat.duration = 500
        val interpolator2: Interpolator = interpolator
        if (interpolator2 != null) {
            ofFloat.interpolator = interpolator2
        }
        animatorSet.play(ofFloat)
        ofFloat.start()
        val stateListener2 = stateListener
        if (stateListener2 != null) {
            if (backdropShown) {
                stateListener2.onOpen(ofFloat)
            } else {
                stateListener2.onClose(ofFloat)
            }
        }
        return ofFloat
    }

    private fun getActionBarSize(): Int {
        val typedValue = TypedValue()
        return if (context!!.theme.resolveAttribute(16843499, typedValue, true)) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, displayMetrics)
        } else -1
    }

    fun open(): ObjectAnimator? {
        return open(null as View?)
    }

    fun open(view: View?): ObjectAnimator? {
        backdropShown = false
        return toggle(view)
    }

    fun close(): ObjectAnimator? {
        backdropShown = true
        return toggle(buttonView)
    }

    private fun updateIcon(view: View) {
        var num: Int
        var num2 = openIcon
//        if (num2 != null && closeIcon.also { num = it!! } != null) {
//            if (num2 == null || num == null || view is ImageView) {
//                val imageView: ImageView = view as ImageView
//                val context2 = context
//                if (backdropShown) {
//                    num2 = num
//                }
//                imageView.setImageDrawable(ContextCompat.getDrawable(context2!!, num2.toInt()))
//                if (closeIcon != null) {
//                    imageView.setColorFilter(
//                        ContextCompat.getColor(context!!, colorIcon!!.toInt()),
//                        PorterDuff.Mode.SRC_IN
//                    )
//                    return
//                }
//                return
//            }
//            Log.e(TAG, "updateIcon() must be called on an ImageView/ImageButton")
//        }
    }

    fun setButtonView(view: View?) {
        buttonView = view
    }

    fun getButtonView(): View? {
        return buttonView
    }

    fun isBackdropShown(): Boolean {
        return backdropShown
    }


}
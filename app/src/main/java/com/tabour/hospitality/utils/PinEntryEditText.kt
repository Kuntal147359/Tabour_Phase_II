package com.tabour.hospitality.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.tabour.hospitality.R

class PinEntryEditText : AppCompatEditText {

    val XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"

    private var mSpace = 24f //24 dp by default, space between the lines

    private var mCharSize = 0f
    private var mNumChars = 4f
    private var mLineSpacing = 8f //8dp by default, height of the text from our lines

    private var mMaxLength = 4

    private var mClickListener: OnClickListener? = null

    private var mLineStroke = 1f //1dp by default

    private var mLineStrokeSelected = 2f //2dp by default

    private var mLinesPaint: Paint? = null

    var mColors = intArrayOf(
        Color.GREEN,
        Color.BLACK,
        Color.GRAY
    )

//    var mColorStates = ColorStateList(mStates, mColors)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(context, attrs!!)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context, attrs!!)
    }

    private fun init(context: Context, attrs: AttributeSet) {

        val multi = context.resources.displayMetrics.density
        mLineStroke = multi * mLineStroke
        mLinesPaint = Paint(paint)
        mLinesPaint!!.strokeWidth = mLineStroke
        mLinesPaint!!.color = resources.getColor(R.color.colorPrimaryDark)
        setBackgroundResource(0)
        mSpace = multi * mSpace //convert to pixels for our density

        mLineSpacing = multi * mLineSpacing //convert to pixels for our density

        mNumChars = mMaxLength.toFloat()

        super.setOnClickListener { v -> // When tapped, move cursor to end of text.
            setSelection(text!!.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }

    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: android.view.ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas);
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }
        var startX = paddingLeft
        val bottom = height - paddingBottom

        //Text Width
        val text = text
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)
        for (i in 0 until mNumChars.toInt()) {
//            updateColorForLines(i == textLength)
            mLinesPaint?.let {
                canvas.drawLine(startX.toFloat(),
                    bottom.toFloat(), startX + mCharSize, bottom.toFloat(), it
                )
            }
            if (getText()!!.length > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(
                    text, i, i + 1, middle - textWidths[0] / 2, bottom - mLineSpacing,
                    paint
                )
            }
            startX += if (mSpace < 0) {
                (mCharSize * 2).toInt()
            } else {
                (mCharSize + mSpace).toInt()
            }
        }
    }

}
package com.tabour.hospitality.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.provider.Settings.Secure
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.tabour.hospitality.R
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.viewmodels.HomeViewModel
import java.util.*
import kotlin.math.roundToInt
import com.google.zxing.qrcode.encoder.ByteMatrix

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder

import com.google.zxing.qrcode.encoder.QRCode





class AppCommonUtils {
    companion object {

        lateinit var geocoder: Geocoder
        lateinit var addresses: List<Address>

        open class NoUnderlineClickSpan(val context: Context) : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.isUnderlineText = false
                textPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
            }

            override fun onClick(widget: View) {}
        }


        fun localeToEmoji(localecode: String): String {
//        val countryCode = locale.country
            val countryCode = localecode
            val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }

        fun hideKeyboard(context: Context) {
            (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

            if (context.currentFocus != null && context.currentFocus!!.windowToken != null) {
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    context.currentFocus!!.windowToken,
                    0
                )
            }
        }

        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context): String {
            return Secure.getString(context.contentResolver,Secure.ANDROID_ID)
        }

        fun loadFragment(context: Context, fragment: Fragment) {
            (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .replace(com.tabour.hospitality.R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        fun loadFragmentWithoutBackstack(context: Context, fragment: Fragment)
        {
            (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .add(com.tabour.hospitality.R.id.nav_host_fragment, fragment)
                .commit()
        }

        fun getPlaceFromLatLng(
            context: Context,
            cityname: String,
            lat: Double,
            longitude: Double
        ): String {
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(lat, longitude, 1) as List<Address>

            var city = ""
            if (cityname.equals("", ignoreCase = true)) {
                city = addresses[0].locality
            } else {
                city = cityname
            }

            val country = addresses[0].countryName

//          Log.d("checkCity", city)

            return city

        }

        fun showCancelBookingDialog(activity: Activity) {
            // Initialize dialog
            val dialog = Dialog(activity)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

            // set custom dialog
            dialog.setContentView(R.layout.redirect_to_login_dialog)

            // set transparent background
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            )

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.getWindow()!!.getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            val img_close: AppCompatImageView = dialog.findViewById(R.id.img_close)
            img_close.setOnClickListener {
                dialog.dismiss()
            }

            val btn_yes: AppCompatTextView = dialog.findViewById(R.id.btn_yes)
            btn_yes.setOnClickListener {
                activity.startActivity(Intent(activity, LoginActivity::class.java))
                activity.finish()
                dialog.dismiss()
            }

            val btn_no: AppCompatTextView = dialog.findViewById(R.id.btn_no)
            btn_no.setOnClickListener {
                dialog.dismiss()
            }
            // show dialog
            dialog.show()

        }

        fun showDeactivateAccDialog(activity: Activity, homeViewModel: HomeViewModel) {
            // Initialize dialog
            val dialog = Dialog(activity)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

            // set custom dialog
            dialog.setContentView(R.layout.deactivate_account_alert_dialog)

            // set transparent background
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            )

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.getWindow()!!.getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            val img_close: AppCompatImageView = dialog.findViewById(R.id.img_close)
            img_close.setOnClickListener {
                dialog.dismiss()
            }

            val btn_yes: AppCompatTextView = dialog.findViewById(R.id.btn_yes)
            btn_yes.setOnClickListener {
                homeViewModel.deactivateAccount(activity)
                activity.finish()
                dialog.dismiss()
            }

            val btn_no: AppCompatTextView = dialog.findViewById(R.id.btn_no)
            btn_no.setOnClickListener {
                dialog.dismiss()
            }
            // show dialog
            dialog.show()

        }

        fun getQrCodeBitmap(qrUrl: String): Bitmap
        {
            val size = 1080 //pixels
            val qrCodeContent = qrUrl
            Log.d("qrCodeContent",qrCodeContent)
//            val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
            var bits: BitMatrix? = null
            var bitmap: Bitmap? = null
            try{
                bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
                bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
                    for (x in 0 until size) {
                        for (y in 0 until size) {
                            it.setPixel(x, y, if (bits!![x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                }
            }catch (e: java.lang.Exception)
            {
                e.printStackTrace()
            }

            return bitmap!!
        }

        fun TextView.setResizableText(
            fullText: String,
            maxLines: Int,
            viewMore: Boolean,
            applyExtraHighlights: ((Spannable) -> (Spannable))? = null
        )
        {
            val width = width
            if (width <= 0) {
                post {
                    setResizableText(fullText, maxLines, viewMore, applyExtraHighlights)
                }
                return
            }

            movementMethod = LinkMovementMethod.getInstance()
            // Since we take the string character by character, we don't want to break up the Windows-style
            // line endings.
            val adjustedText = fullText.replace("\r\n", "\n")
            // Check if even the text has to be resizable.
            val textLayout = StaticLayout(
                adjustedText,
                paint,
                width - paddingLeft - paddingRight,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                includeFontPadding
            )

            if (textLayout.lineCount <= maxLines || adjustedText.isEmpty()) {
                // No need to add 'read more' / 'read less' since the text fits just as well (less than max lines #).
                val htmlText = adjustedText.replace("\n", "<br/>")
                text = addClickablePartTextResizable(
                    fullText,
                    maxLines,
                    HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
                    null,
                    viewMore,
                    applyExtraHighlights
                )
                return
            }

            val charactersAtLineEnd = textLayout.getLineEnd(maxLines - 1)
            val suffixText =
                if (viewMore) resources.getString(R.string.resizable_text_read_more) else resources.getString(R.string.resizable_text_read_less)
            var charactersToTake = charactersAtLineEnd - suffixText.length / 2 // Good enough first guess
            if (charactersToTake <= 0) {
                // Happens when text is empty
                val htmlText = adjustedText.replace("\n", "<br/>")
                text = addClickablePartTextResizable(
                    fullText,
                    maxLines,
                    HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
                    null,
                    viewMore,
                    applyExtraHighlights
                )
                return
            }

            if (!viewMore) {
                // We can set the text immediately because nothing needs to be measured
                val htmlText = adjustedText.replace("\n", "<br/>")
                text = addClickablePartTextResizable(
                    fullText,
                    maxLines,
                    HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
                    suffixText,
                    viewMore,
                    applyExtraHighlights
                )
                return
            }

            val lastHasNewLine =
                adjustedText.substring(textLayout.getLineStart(maxLines - 1), textLayout.getLineEnd(maxLines - 1))
                    .contains("\n")
            var linedText = if (lastHasNewLine) {
                val charactersPerLine =
                    textLayout.getLineEnd(0) / (textLayout.getLineWidth(0) / textLayout.ellipsizedWidth.toFloat())
                val lineOfSpaces =
                    "\u00A0".repeat(charactersPerLine.roundToInt()) // non breaking space, will not be thrown away by HTML parser
                charactersToTake += lineOfSpaces.length - 1
                adjustedText.take(textLayout.getLineStart(maxLines - 1)) +
                        adjustedText.substring(textLayout.getLineStart(maxLines - 1), textLayout.getLineEnd(maxLines - 1))
                            .replace("\n", lineOfSpaces) +
                        adjustedText.substring(textLayout.getLineEnd(maxLines - 1))
            } else {
                adjustedText
            }

            // Check if we perhaps need to even add characters? Happens very rarely, but can be possible if there was a long word just wrapped
            val shortenedString = linedText.take(charactersToTake)
            val shortenedStringWithSuffix = shortenedString + suffixText
            val shortenedStringWithSuffixLayout = StaticLayout(
                shortenedStringWithSuffix,
                paint,
                width - paddingLeft - paddingRight,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                includeFontPadding
            )

            val modifier: Int
            if (shortenedStringWithSuffixLayout.getLineEnd(maxLines - 1) >= shortenedStringWithSuffix.length) {
                modifier = 1
                charactersToTake-- // We might just be at the right position already
            } else {
                modifier = -1
            }

            do {
                charactersToTake += modifier
                val baseString = linedText.take(charactersToTake)
                val appended = baseString + suffixText
                val newLayout = StaticLayout(
                    appended,
                    paint,
                    width - paddingLeft - paddingRight,
                    Layout.Alignment.ALIGN_NORMAL,
                    lineSpacingMultiplier,
                    lineSpacingExtra,
                    includeFontPadding
                )
            } while ((modifier < 0 && newLayout.getLineEnd(maxLines - 1) < appended.length) ||
                (modifier > 0 && newLayout.getLineEnd(maxLines - 1) >= appended.length)
            )

            if (modifier > 0) {
                charactersToTake-- // We went overboard with 1 char, fixing that
            }

            // We need to convert newlines because we are going over to HTML now
            val htmlText = linedText.take(charactersToTake).replace("\n", "<br/>")
            text = addClickablePartTextResizable(
                fullText,
                maxLines,
                HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
                suffixText,
                viewMore,
                applyExtraHighlights
            )
        }

        fun TextView.addClickablePartTextResizable(
            fullText: String,
            maxLines: Int,
            shortenedText: Spanned,
            clickableText: String?,
            viewMore: Boolean,
            applyExtraHighlights: ((Spannable) -> (Spannable))? = null
        ): Spannable
        {
            val builder = SpannableStringBuilder(shortenedText)
            if (clickableText != null) {
                builder.append(clickableText)
                val startIndexOffset = 0 // Do not highlight the 3 dots and the space
                builder.setSpan(object : NoUnderlineClickSpan(context) {
                    override fun onClick(widget: View) {
                        if (viewMore) {
                            setResizableText(fullText, maxLines, false, applyExtraHighlights)
                        } else {
                            setResizableText(fullText, maxLines, true, applyExtraHighlights)
                        }
                    }
                }, builder.indexOf(clickableText) + startIndexOffset, builder.indexOf(clickableText) + clickableText.length, 0)
            }
            if (applyExtraHighlights != null) {
                return applyExtraHighlights(builder)
            }
            return builder

        }



        fun clearSession(){
            AppPreferenceStorage.saveUserid("")
            AppPreferenceStorage.saveAuthToken("")
            AppPreferenceStorage.saveCount("")
            AppPreferenceStorage.saveUsername("")
            AppPreferenceStorage.saveMobileNum("")
            AppPreferenceStorage.saveProfilePic("")
        }

        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }

//        fun loadImageWithCorners(url: String?, view: ImageView) {
//            Glide.with(context!!)
//                .load(url)
//                .asBitmap()
//                .centerCrop()
//                .placeholder(com.tabour.hospitality.R.color.gray)
//                .error(com.tabour.hospitality.R.color.gray)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(object : BitmapImageViewTarget(view) {
//                    override fun setResource(resource: Bitmap?) {
//                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(
//                            context!!.resources, resource
//                        )
//                        circularBitmapDrawable.cornerRadius = 32.0f // radius for corners
//                        view.setImageDrawable(circularBitmapDrawable)
//                    }
//                })
//        }

        fun showNetworkConnectivityDialog(activity: Activity?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
            dialog.setContentView(R.layout.dialoginternetconnection)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.findViewById<View>(R.id.btn_yes).setOnClickListener {
                dialog.dismiss()
                AppCommonUtils.hideKeyboard(activity)
                activity.finish()

            }
            dialog.show()
            dialog.window!!.attributes = lp
        }

        fun clearAllSession()
        {
            AppPreferenceStorage.saveUserid("")
            AppPreferenceStorage.saveAuthToken("")
            AppPreferenceStorage.saveCount("")
            AppPreferenceStorage.saveUsername("")
            AppPreferenceStorage.saveMobileNum("")
            AppPreferenceStorage.saveProfilePic("")
        }

        fun getCurrentActivity(context: Context): String
        {
            val am: ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            Log.d(
                "checkActivity",
                "CURRENT Activity ::" + taskInfo[0].topActivity!!.shortClassName + "   Package Name :  " + componentInfo!!.packageName
            )
            return taskInfo[0].topActivity!!.shortClassName
        }
    }
}
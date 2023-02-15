package com.tabour.hospitality.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.alimuzaffar.lib.pin.PinEntryEditText
import com.tabour.hospitality.R
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otpverify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OTPVerify : AppCompatActivity() {

    lateinit var lyt_go_back: LinearLayout
    lateinit var txt_pin_entry: PinEntryEditText
    lateinit var tv_phnumber: TextView
    lateinit var tv_goback: TextView
    lateinit var tv_resend_otp: TextView
    lateinit var tv_timer: TextView
    lateinit var termsofuse: TextView
    lateinit var sendbutton: AppCompatTextView
    lateinit var loginViewModel: LoginViewModel
    lateinit var device_id: String
    lateinit var mWaitDialog: AppWaitDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverify)

        // lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        LoginViewModel.loginStatus.value = false
        LoginViewModel.userstatus.value = ""

        initViews()

        loginViewModel.sendOtp(this, "${LoginViewModel.countryCode.value!!}${LoginViewModel.onlyphnNumber.value!!}")

        LoginViewModel.otp.observe(this) {
            if(!it.equals(""))
            {
                showtimer()
//                sendbutton.isEnabled = true
                enableBtnNext("1")
            }
//            tv_otp.text = LoginViewModel.otp.value!!
        }

        LoginViewModel.userstatus.observe(this){

            Log.d("checkUserStatus", it)

            if(it.equals("0", ignoreCase = true))
            {
                // Go to Register
                val registerIntent = Intent(this, RegisterActivity::class.java)
                startActivity(registerIntent)
                finish()
            }
            else if(it.equals("1", ignoreCase = true))
            {
                // Go to Main
                loginViewModel.login(this, LoginViewModel.onlyphnNumber.value!!)
            }
        }

        LoginViewModel.loginStatus.observe(this){
            Log.d("checkloginparam", "1. login status is ${LoginViewModel.loginStatus.value}")
            if(it)
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("mode","main")
                startActivity(intent)
                finish()
            }
        }

        LoginViewModel.progressbarObservable.observe(this){
            if (it) {
                mWaitDialog.setMessage("please wait...")
                mWaitDialog.show()
            } else {
                mWaitDialog.dismiss()
            }
        }
    }

    private fun showtimer() {
        val timer = object : CountDownTimer(180000,1000){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("currentThread","${Thread.currentThread()}")
                tv_resend_otp.visibility = View.GONE
                showTimeinMinutes(millisUntilFinished)
            }

            override fun onFinish() {
//                sendbutton.isEnabled = false
                enableBtnNext("0")
                LoginViewModel.otp.value = ""
                tv_resend_otp.visibility = View.VISIBLE
            }
        }
        timer.start()
    }

    private fun showTimeinMinutes(milliseconds: Long) {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        tv_timer.text = "$minutes:$seconds"

        Log.d("timer","$minutes:$seconds")
    }

    fun enableBtnNext(status: String)
    {
        if (status.equals("1"))
        {
            sendbutton.isEnabled = true
            sendbutton.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent))
            Log.d("checkenable",status)
        }
        else
        {
            sendbutton.isEnabled = false
            sendbutton.setBackgroundColor(ContextCompat.getColor(this,R.color.faintsandybrown))
            Log.d("checkenable",status)
        }
    }

    fun initViews(){
        mWaitDialog = AppWaitDialog(this)

        tv_phnumber = findViewById(R.id.tv_phnumber)
        tv_phnumber.text = LoginViewModel.phoneNumber.value!!
        tv_goback = findViewById(R.id.tv_goback)
        tv_goback.setOnClickListener {
            AppCommonUtils.hideKeyboard(this@OTPVerify)
            startActivity(Intent(this@OTPVerify, LoginActivity::class.java))
            finish()
        }
        tv_timer = findViewById(R.id.tv_timer)
        termsofuse = findViewById(R.id.termsofuse)

        tv_resend_otp = findViewById(R.id.tv_resend_otp)
        tv_resend_otp.setOnClickListener {
            txt_pin_entry.setText("")
            loginViewModel.sendOtp(this, "${LoginViewModel.countryCode.value!!}${LoginViewModel.onlyphnNumber.value!!}")
        }

        // Terms of Use
        val text: String = getString(R.string.the_terms_of_use)

        val fcs = ForegroundColorSpan(Color.rgb(255, 255, 255))
        val bss = StyleSpan(Typeface.BOLD)
        val spannable: Spannable = SpannableString(text)

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        spannable.setSpan(clickableSpan, 4, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(
            fcs,
            4,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            bss,
            4,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        termsofuse.text = spannable

        sendbutton = findViewById(R.id.sendbutton)
        sendbutton.setOnClickListener {
            device_id = Settings.Secure.ANDROID_ID
            val entered_pin = txt_pin_entry.text.toString()
            if (entered_pin.equals(LoginViewModel.otp.value!!)) {
                loginViewModel.verifyUser(this@OTPVerify, LoginViewModel.onlyphnNumber.value!!,device_id)
            } else {
                Toast.makeText(this@OTPVerify, "Invalid Otp", Toast.LENGTH_SHORT).show()
                txt_pin_entry.setText(null)
            }
        }

        lyt_go_back = findViewById(R.id.lyt_go_back)
        lyt_go_back.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        txt_pin_entry = findViewById(R.id.txt_pin_entry)
        txt_pin_entry.setOnPinEnteredListener(object : PinEntryEditText.OnPinEnteredListener {
            override fun onPinEntered(str: CharSequence?) {
//                device_id = Settings.Secure.ANDROID_ID
//                if (str.toString().equals(LoginViewModel.otp.value!!)) {
//                    loginViewModel.verifyUser(this@OTPVerify, LoginViewModel.onlyphnNumber.value!!,device_id)
//                } else {
//                    Toast.makeText(this@OTPVerify, "Invalid Otp", Toast.LENGTH_SHORT).show()
//                    txt_pin_entry.setText(null)
//                }
            }
        })
    }

//    override fun onStop() {
//        super.onStop()
//
//        LoginViewModel.userstatus.removeObservers(this)
//
//    }


}
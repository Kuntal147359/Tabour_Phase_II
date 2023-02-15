package com.tabour.hospitality.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.tabour.hospitality.R
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    lateinit var lyt_contn_as_another_num: LinearLayout
    lateinit var tv_email: TextView
    lateinit var tv_num_not_found: TextView
    lateinit var trms_cndtns: TextView
    lateinit var edt_fstname: EditText
    lateinit var edt_lstname: EditText
    lateinit var edt_email: EditText
    lateinit var trmsncndtns: CheckBox
    lateinit var btn_register: AppCompatButton
    lateinit var loginViewModel: LoginViewModel
    lateinit var country_code: String
    lateinit var phone_number: String
    lateinit var mWaitDialog: AppWaitDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        country_code = LoginViewModel.countryCode.value!!
        phone_number = LoginViewModel.onlyphnNumber.value!!

        initViews()

        LoginViewModel.registerUserData.observe(this) {
            if (TextUtils.isEmpty(it.fname)) {
                edt_fstname.setError("Enter first name")
                edt_fstname.requestFocus()
            } else if (TextUtils.isEmpty(it.lname)) {
                edt_lstname.setError("Enter last name")
                edt_lstname.requestFocus()
            } else if (!trmsncndtns.isChecked) {
                Toast.makeText(
                    this,
                    "Please agree terms and conditions",
                    Toast.LENGTH_LONG
                ).show()
            }
            else
            {
                loginViewModel.registerUser(
                    this,
                    it.fname,
                    it.lname,
                    it.email,
                    country_code,
                    phone_number
                )
            }
        }

        LoginViewModel.loginStatus.observe(this){
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

    @SuppressLint("ResourceAsColor")
    fun initViews() {
        mWaitDialog = AppWaitDialog(this)
        tv_email = findViewById(R.id.tv_email)
        tv_num_not_found = findViewById(R.id.tv_num_not_found)
        trms_cndtns = findViewById(R.id.trms_cndtns)

        // Spannable
        val phnWithCode = "${country_code} ${phone_number}"
        val text: String = String.format(getString(R.string.number_not_found), "${country_code} ${phone_number}")

        val fcs = ForegroundColorSpan(Color.rgb(64, 121, 140))
        val bss = StyleSpan(Typeface.BOLD)
        val spannable: Spannable = SpannableString(text)

        spannable.setSpan(
            fcs,
            12,
            12 + phnWithCode.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            bss,
            12,
            12 + phnWithCode.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tv_num_not_found.setText(spannable, TextView.BufferType.SPANNABLE)
//        tv_num_not_found.text = String.format(getString(R.string.number_not_found), "${country_code} ${phone_number}")


        // Spannable for Email
//        val emailtext: String = getString(R.string.email_address_optional)
//
//        val emailbss = StyleSpan(Typeface.NORMAL)
//        val emailspannable: Spannable = SpannableString(emailtext)
//
//        emailspannable.setSpan(
//            emailbss,
//            15,
//            emailtext.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//
//        tv_email.setText(emailspannable, TextView.BufferType.SPANNABLE)

        // Spannable for I agree terms of conditions
        val trms_text: String = getString(R.string.terms_of_use)
        val trms_fcs = ForegroundColorSpan(Color.rgb(64, 121, 140))
        val trms_bss = StyleSpan(Typeface.BOLD)

        val trms_spannable: Spannable = SpannableString(trms_text)

        trms_spannable.setSpan(
            trms_fcs,
            15,
            trms_text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        trms_spannable.setSpan(
            trms_bss,
            15,
            trms_text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        trms_cndtns.setText(trms_spannable, TextView.BufferType.SPANNABLE)
        edt_fstname = findViewById(com.tabour.hospitality.R.id.edt_fstname)
        edt_lstname = findViewById(com.tabour.hospitality.R.id.edt_lstname)
        edt_email = findViewById(com.tabour.hospitality.R.id.edt_email)
        trmsncndtns = findViewById(com.tabour.hospitality.R.id.trmsncndtns)
        lyt_contn_as_another_num = findViewById(com.tabour.hospitality.R.id.lyt_contn_as_another_num)
        lyt_contn_as_another_num.setOnClickListener {
            AppCommonUtils.hideKeyboard(this)
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        btn_register = findViewById(com.tabour.hospitality.R.id.btn_register)
        btn_register.setOnClickListener {
            AppCommonUtils.hideKeyboard(this)
            loginViewModel.checkRegister(
                edt_fstname.text.toString(),
                edt_lstname.text.toString(),
                edt_email.text.toString(),
                country_code,
                phone_number
            )

//            loginViewModel.login(this, "123456789")

        }
    }
}
package com.tabour.hospitality.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.CountryWithFlagAdapter
import com.tabour.hospitality.base.MyApplicationClass.Companion.context
import com.tabour.hospitality.models.Countries
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.viewmodels.LoginViewModel
import java.util.*


class LoginActivity : AppCompatActivity() {

    lateinit var lyt_contn_as_guest: LinearLayout
    lateinit var testView: TextView
    lateinit var country_phn_code: TextView
    lateinit var phone_edt: EditText
    lateinit var termsofuse: TextView
    lateinit var country_flag: ImageView
    lateinit var btn_continue: AppCompatButton
    lateinit var loginViewModel: LoginViewModel
    lateinit var adapter: CountryWithFlagAdapter
    lateinit var country_codes: ArrayList<Countries>
    var num: Int = 966
    var mResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        initViews()

        Log.d("checkfontsize",AppCommonUtils.pixelsToSp(this, 26F).toString())

        loginViewModel.getCountriesList(this)

        LoginViewModel.countriesData.observe(this, androidx.lifecycle.Observer {
            initCountries()
        })

        val fileName = String.format("f%03d", num)
        mResId = context!!.applicationContext.resources.getIdentifier(
            fileName,
            "drawable",
            context!!.applicationContext.packageName
        )
    }

    fun initViews() {
        testView = findViewById(R.id.testView)
        country_phn_code = findViewById(R.id.country_phn_code)
        phone_edt = findViewById(R.id.phone_edt)
        termsofuse = findViewById(R.id.termsofuse)
//        email_edt.setText(localeToEmoji(Locale.US))

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

        testView.setOnClickListener {
            showCountryCodesDialog()
        }

        lyt_contn_as_guest = findViewById(R.id.lyt_contn_as_guest)
        lyt_contn_as_guest.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("mode","main")
            startActivity(intent)
            finish()
        }

        btn_continue = findViewById(R.id.btn_continue)
        btn_continue.setOnClickListener {

            if(TextUtils.isEmpty(phone_edt.text.toString()))
            {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_LONG).show()
            }
            else
            {
                AppCommonUtils.hideKeyboard(this@LoginActivity)
                val full_number = "${country_phn_code.text.toString()} ${phone_edt.text.toString()}"
                LoginViewModel.countryCode.postValue(country_phn_code.text.toString())
                LoginViewModel.phoneNumber.postValue(full_number)
                LoginViewModel.onlyphnNumber.postValue(phone_edt.text.toString())
                startActivity(Intent(this, OTPVerify::class.java))
                finish()
            }

        }

    }

    fun initCountries() {

        // Default flag
        var selected_item = ""
        var selected_code = ""
        country_codes = ArrayList<Countries>()
        for (i in 0 until LoginViewModel.countriesData.value!!.size) {
            if (LoginViewModel.countriesData.value!!.get(i).iso2.equals("QA", ignoreCase = true)) {
                selected_item = LoginViewModel.countriesData.value!!.get(i).iso2
                selected_code = LoginViewModel.countriesData.value!!.get(i).phonecode
//                countryCode_id = signInViewModel.countriesList.value!!.get(i).id
            }
        }

        adapter = CountryWithFlagAdapter(
            this,
            R.layout.country_with_flag_row,
            LoginViewModel.countriesData.value!!
        )

        testView.setText(
            AppCommonUtils.localeToEmoji(selected_item)
        )
        country_phn_code.text = selected_code

    }

    @SuppressLint("CutPasteId")
    fun showCountryCodesDialog() {
        // Initialize dialog
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.lyt_cntrycodes)

        // set transparent background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edt_cntry_code: EditText = dialog.findViewById(R.id.edt_cntry_code)
        val codes_list_view: ListView = dialog.findViewById(R.id.codes_list_view)

        adapter = CountryWithFlagAdapter(
            this,
            R.layout.country_with_flag_row,
            LoginViewModel.countriesData.value!!
        )

        codes_list_view.adapter = adapter

        edt_cntry_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.getFilter().filter(s)
            }

            override fun afterTextChanged(editable: Editable?) {
                adapter.getFilter().filter(editable)
            }

        })

        codes_list_view.setOnItemClickListener { parent, view, position, id ->

            val tv_cntry_flag = view.findViewById<TextView>(R.id.country_flag)
            val tv_phn_code = view.findViewById<TextView>(R.id.country_phn_code)
            testView.setText(
                tv_cntry_flag.text.toString()
            )
            country_phn_code.setText(tv_phn_code.text.toString())

            dialog.dismiss()
        }

//        codes_list_view.setOnItemClickListener(object : AdapterView.OnItemClickListener {
//            override fun onItemClick(parent: AdapterView<*>, p1: View?, position: Int, p3: Long) {
//
//                val countryItem: Countries = LoginViewModel.countriesData.value!!.get(position)
//                testView.setText(
//                    AppCommonUtils.localeToEmoji(countryItem.iso2)
//                )
//                country_phn_code.setText(countryItem.phonecode)
//
////                val country_lst = signInViewModel.countriesList.value!!
////                for (clist in country_lst) {
////                    if (countryItem.contains(clist.iso2, ignoreCase = true)) {
////                        countryCode_id = clist.id
////                    }
////                }
//
//                dialog.dismiss()
//            }
//        })

        // show dialog
        dialog.show()

    }

    private fun GetCountryZipCode(ssid: String): String? {
        val loc = Locale("", ssid)
        return loc.getDisplayCountry().trim()
    }

    val Locale.flagEmoji: String
        get() {
            val firstLetter = Character.codePointAt(country, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(country, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }

    fun localeToEmoji(locale: Locale): String {
//        val countryCode = locale.country
        val countryCode = "SA"
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

}
package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.tabour.hospitality.R
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.LoginViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [OccasionFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OccasionFormFragment : Fragment() {

    lateinit var root: View
    lateinit var lyt_back: LinearLayout
    lateinit var tv_choice: TextView
    lateinit var edt_occasion: EditText
    lateinit var edt_specialRqst: EditText
    lateinit var btnNext: AppCompatTextView
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_occasion_form, container, false)
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)

        initViews()

        return root
    }

    @SuppressLint("SetTextI18n")
    fun initViews() {
        tv_choice = root.findViewById(R.id.tv_choice)

        val text = EshtblshmntViewModel.booking_time_seating.value!!
        val fcs = ForegroundColorSpan(Color.rgb(64, 121, 140))
        val spannable: Spannable = SpannableString(EshtblshmntViewModel.booking_time_seating.value!!)
        val index = text.indexOf("•")

        spannable.setSpan(
            fcs,
            index,
            index + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tv_choice.text = spannable
        edt_occasion = root.findViewById(R.id.edt_occasion)
        edt_specialRqst = root.findViewById(R.id.edt_specialRqst)
        lyt_back = root.findViewById(R.id.lyt_back)

        // system's back events
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            Log.i("Keycheck", "keyCode: $keyCode")
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onBackPressed()
                return@OnKeyListener true
            }
            false
        })

        lyt_back.setOnClickListener {
            onBackPressed()
        }

        btnNext = root.findViewById(R.id.btnNext)
        btnNext.setOnClickListener {

//            if (TextUtils.isEmpty(edt_occasion.text.toString())) {
//                edt_occasion.setError(getString(R.string.entr_occasion))
//            } else {
//                EshtblshmntViewModel.occasion.value = edt_occasion.text.toString()
//                EshtblshmntViewModel.special_request.value = edt_specialRqst.text.toString()
//
//                AppCommonUtils.hideKeyboard(requireActivity())
//                AppCommonUtils.loadFragment(requireActivity(), ConfirmBookingFragment())
//
//            }

            EshtblshmntViewModel.occasion.value = edt_occasion.text.toString().trim()
            EshtblshmntViewModel.special_request.value = edt_specialRqst.text.toString().trim()

            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(), ConfirmBookingFragment())

        }
    }

    fun onBackPressed() {
//        AppCommonUtils.hideKeyboard(requireActivity())
//        val maneger: FragmentManager = requireActivity().supportFragmentManager
//        maneger.beginTransaction()
//            .remove(this)
//            .commit()
//        maneger.popBackStack()

        AppCommonUtils.hideKeyboard(requireActivity())
        AppCommonUtils.loadFragment(requireActivity(), HomeFragment())

    }


}
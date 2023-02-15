package com.tabour.hospitality.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.tabour.hospitality.R
import com.tabour.hospitality.databinding.FragmentConfirmBookingBinding
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.booking_date
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.dining_area
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.no_guest
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.occasion
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.special_request
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.time_slot

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmBookingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfirmBookingFragment : Fragment() {

    lateinit var root :View
    lateinit var lyt_back: LinearLayout
    lateinit var btnConfirmBooking: AppCompatTextView
    lateinit var trmsncndtns: CheckBox
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var binding: FragmentConfirmBookingBinding
    lateinit var mWaitDialog: AppWaitDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_confirm_booking, container, false)
        root = binding.root
        binding.lifecycleOwner = this

        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        EshtblshmntViewModel.reservation_id.value = ""

        initViews()

        EshtblshmntViewModel.reservation_id.observe(viewLifecycleOwner){
            if(!it.equals(""))
            {
                AppPreferenceStorage.saveCount("")
                AppCommonUtils.hideKeyboard(requireActivity())
                val bundle = Bundle()
                bundle.putString("reservation_id", it)
                bundle.putString("category", "1")
                val viewBooking = ViewBooking()
                viewBooking.arguments = bundle
                AppCommonUtils.loadFragment(requireActivity(), viewBooking)
            }
        }

        EshtblshmntViewModel.progressbar.observe(viewLifecycleOwner){
            if (it) {
                mWaitDialog.setMessage("please wait...")
                mWaitDialog.show()
            } else {
                mWaitDialog.dismiss()
            }
        }

        return root
    }

    fun initViews()
    {
        mWaitDialog = AppWaitDialog(requireContext())
        trmsncndtns = root.findViewById(R.id.trmsncndtns)

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
        btnConfirmBooking = root.findViewById(R.id.btnConfirmBooking)
        btnConfirmBooking.setOnClickListener {

            eshtblshmntViewModel.submitBooking(
                requireContext(),
                EshtblshmntViewModel.booking_date.value!!,
                EshtblshmntViewModel.time_slot.value!!,
                EshtblshmntViewModel.restro_id.value!!,
                EshtblshmntViewModel.no_guest.value!!,
                EshtblshmntViewModel.occasion.value!!,
                EshtblshmntViewModel.dining_area_id.value!!,
                EshtblshmntViewModel.special_request.value!!
            )
        }
    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        val maneger: FragmentManager = requireActivity().supportFragmentManager
        maneger.beginTransaction()
            .remove(this)
            .commit()
        maneger.popBackStack()
    }

}
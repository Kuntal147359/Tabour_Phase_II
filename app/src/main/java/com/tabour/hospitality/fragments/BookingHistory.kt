package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.BookinHistoryAdapter
import com.tabour.hospitality.adapters.EshtablishmentAdapter
import com.tabour.hospitality.databinding.FragmentBookingHistoryBinding
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.fragment_booking_history.*
import kotlinx.android.synthetic.main.fragment_search_restauants.*

/**
 * A simple [Fragment] subclass.
 * Use the [BookingHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingHistory : Fragment() {
    lateinit var root: View
    lateinit var toolbar: Toolbar
//    lateinit var tv_upcmg_booking: TextView
//    lateinit var tv_history_booking: TextView
    lateinit var bottomNav: BottomNavigationView
//    lateinit var lyt_back: LinearLayout
//    lateinit var lyt_upcmg_booking: LinearLayout
//    lateinit var lyt_history_booking: LinearLayout
//    lateinit var booking_history_recy: RecyclerView
    lateinit var bookinHistoryAdapter: BookinHistoryAdapter
    lateinit var binding: FragmentBookingHistoryBinding
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var mWaitDialog: AppWaitDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingHistoryBinding.inflate(layoutInflater)
        root = binding.root
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        initView()
        eshtblshmntViewModel.getBookingHistory(requireContext(),"upcoming")

        EshtblshmntViewModel.histryBookngListData.observe(viewLifecycleOwner){
            initNearbyResRecyclerView()
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

    private fun initView() {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
//        lyt_back = root.findViewById(R.id.lyt_back)
//        tv_upcmg_booking = root.findViewById(R.id.tv_upcmg_booking)
//        tv_history_booking = root.findViewById(R.id.tv_history_booking)
//        booking_history_recy = root.findViewById(R.id.booking_history_recy)
////      lyt_upcmg_booking = root.findViewById(R.id.lyt_upcmg_booking)
//        lyt_history_booking = root.findViewById(R.id.lyt_history_booking)

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

        binding.lytBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvUpcmgBooking.setOnClickListener {
            lyt_upcmg_booking.setBackgroundResource(R.drawable.booking_history_button_bg)
            tv_upcmg_booking.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            lyt_history_booking.setBackgroundResource(0)
            tv_history_booking.setTextColor(ContextCompat.getColor(requireContext(),R.color.h_text_color))
            eshtblshmntViewModel.getBookingHistory(requireContext(),"upcoming")
        }

        binding.tvHistoryBooking.setOnClickListener {
            lyt_upcmg_booking.setBackgroundResource(0)
            tv_upcmg_booking.setTextColor(ContextCompat.getColor(requireContext(),R.color.h_text_color))
            lyt_history_booking.setBackgroundResource(R.drawable.booking_history_button_bg)
            tv_history_booking.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            eshtblshmntViewModel.getBookingHistory(requireContext(),"history")
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initNearbyResRecyclerView() {
        bookinHistoryAdapter = BookinHistoryAdapter(
            requireActivity(),
            requireContext(),
            EshtblshmntViewModel.histryBookngListData.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        booking_history_recy.smoothScrollToPosition(0)
        booking_history_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext()
            )
        )
        booking_history_recy.setAdapter(bookinHistoryAdapter)
        bookinHistoryAdapter.notifyDataSetChanged()
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
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
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.BookinHistoryAdapter
import com.tabour.hospitality.adapters.NearbyResAdapter
import com.tabour.hospitality.adapters.SavedResAdapter
import com.tabour.hospitality.databinding.FragmentBookingHistoryBinding
import com.tabour.hospitality.databinding.FragmentSavedRestaurantsBinding
import com.tabour.hospitality.models.SavedRestaurant
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.utils.ItemClickListener
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.fragment_booking_history.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_saved_restaurants.*

/**
 * A simple [Fragment] subclass.
 * Use the [SavedRestaurants.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedRestaurants : Fragment(), ItemClickListener {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var tv_trynew: TextView
    lateinit var tv_bookagain: TextView
    lateinit var bottomNav: BottomNavigationView
    lateinit var lyt_trynew: LinearLayout
    lateinit var lyt_bookagain: LinearLayout
    lateinit var saved_res_recy: RecyclerView
    lateinit var savedResAdapter: SavedResAdapter
    lateinit var binding: FragmentSavedRestaurantsBinding
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var mWaitDialog: AppWaitDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSavedRestaurantsBinding.inflate(layoutInflater)
        root = binding.root
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        EshtblshmntViewModel.userisinqueueData.value = null

        initViews()

        eshtblshmntViewModel.getSavedRestaurants(requireContext(), "try_new")

        EshtblshmntViewModel.savedResListData.observe(viewLifecycleOwner){
            initSavedResRecyclerView(it)
        }

        EshtblshmntViewModel.userisinqueueData.observe(viewLifecycleOwner)
        {
            EshtblshmntViewModel.userisinqueueData.value?.let {

                when(it.user_is_in_queue)
                {
                    "1" -> {
                        val bundle = Bundle()
                        bundle.putString("mode","user_in_queue")
                        bundle.putString("category", "1")
                        bundle.putString("restro_id",it.res_id)
                        bundle.putString("dinning_area_id","")
                        bundle.putString("user_queue_id",it.user_queue_id)
                        val viewQueueBooking = ViewQueueBooking()
                        viewQueueBooking.arguments = bundle
                        AppCommonUtils.loadFragment(requireActivity(),viewQueueBooking)
                    }
                    "0" -> {
                        val bundle = Bundle()
                        bundle.putString("restro_id",it.res_id)
                        val chooseseatingQueueFragment = ChooseseatingQueueFragment()
                        chooseseatingQueueFragment.arguments = bundle
                        AppCommonUtils.loadFragment(requireActivity(),chooseseatingQueueFragment)
                    }
                    else ->{

                        Toast.makeText(context, "Restaurant is currently closed for queue booking", Toast.LENGTH_LONG).show()

                    }
                }
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

    private fun initViews() {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.VISIBLE
        tv_trynew = root.findViewById(R.id.tv_trynew)
        tv_bookagain = root.findViewById(R.id.tv_bookagain)
        saved_res_recy = root.findViewById(R.id.saved_res_recy)
        lyt_trynew = root.findViewById(R.id.lyt_trynew)
        lyt_bookagain = root.findViewById(R.id.lyt_bookagain)

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

        lyt_trynew.setOnClickListener {
            lyt_trynew.setBackgroundResource(R.drawable.booking_history_button_bg)
            tv_trynew.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            lyt_bookagain.setBackgroundResource(0)
            tv_bookagain.setTextColor(ContextCompat.getColor(requireContext(),R.color.h_text_color))
            eshtblshmntViewModel.getSavedRestaurants(requireContext(), "try_new")
        }

        lyt_bookagain.setOnClickListener {
            lyt_trynew.setBackgroundResource(0)
            tv_trynew.setTextColor(ContextCompat.getColor(requireContext(),R.color.h_text_color))
            lyt_bookagain.setBackgroundResource(R.drawable.booking_history_button_bg)
            tv_bookagain.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            eshtblshmntViewModel.getSavedRestaurants(requireContext(), "book_again")
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initSavedResRecyclerView(it: ArrayList<SavedRestaurant>) {
        savedResAdapter = SavedResAdapter(
            requireActivity(),
            requireContext(),
            it
        )

        //expertListRecyclerView.setHasFixedSize(true)
        saved_res_recy.smoothScrollToPosition(0)
        saved_res_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext()
            )
        )
        saved_res_recy.setAdapter(savedResAdapter)
        savedResAdapter.notifyDataSetChanged()
        savedResAdapter.setClickListener(this)
    }

    fun onBackPressed() {
        requireActivity().finish()
    }

    override fun onClick(view: View?, module: String, position: Int) {
        when(module)
        {
            "checkqueueAvail" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    eshtblshmntViewModel.checkUserInQueue(requireContext(), EshtblshmntViewModel.savedResListData.value!!.get(position).id.toString())
                }
            }
        }
    }

}
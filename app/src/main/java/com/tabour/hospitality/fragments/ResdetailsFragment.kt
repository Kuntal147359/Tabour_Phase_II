package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.EshtablishmentAdapter
import com.tabour.hospitality.adapters.ParentItemAdapter
import com.tabour.hospitality.adapters.RestaurantImageSliderAdapter
import com.tabour.hospitality.adapters.WorkingHoursAdapter
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppCommonUtils.Companion.setResizableText
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SearchViewModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.android.synthetic.main.edit_profile_view.*
import kotlinx.android.synthetic.main.explore_row.*
import kotlinx.android.synthetic.main.fragment_resdetails.*
import kotlinx.android.synthetic.main.fragment_search_restauants.*
import kotlinx.android.synthetic.main.include_drawer_content.*


/**
 * A simple [Fragment] subclass.
 * Use the [ResdetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResdetailsFragment : Fragment(), OnMapReadyCallback {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var toolbar_resDtls: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var menu: Menu
    lateinit var menuItem: MenuItem
    lateinit var bottom_sheet: View
    lateinit var tv_restaurant_name: AppCompatTextView
    lateinit var mBehavior: BottomSheetBehavior<View>
    lateinit var mBottomSheetDialog: BottomSheetDialog
    lateinit var lyt_book_now: LinearLayout
    lateinit var lyt_join_queue: LinearLayout
    lateinit var notification_bell: LinearLayout
    lateinit var img_save: AppCompatImageView
    lateinit var parentRecyclerViewItem: RecyclerView
    lateinit var working_hrs_recy: RecyclerView
    lateinit var tv_restaurant_description: TextView
    lateinit var tv_cusines: TextView
    lateinit var tv_seating: TextView
    lateinit var tv_second_res_name: TextView
    lateinit var tv_rest_location: TextView
    lateinit var tv_restaurant_open_status: TextView
    lateinit var tv_restaurant_close_status: TextView
    lateinit var tv_restaurant_parking: TextView
    lateinit var lyt_restaurant_close_status: LinearLayout
    lateinit var vp_restaurant_details: LinearLayout
    lateinit var vp_restaurant_menu: LinearLayout
    lateinit var iv_back: AppCompatImageView
    lateinit var tab_layout: TabLayout
    lateinit var tab_1: TabLayout.Tab
    lateinit var tab_2: TabLayout.Tab
    lateinit var vp_restaurant_banner: ViewPager2
    lateinit var dots_indicator: DotsIndicator
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var workingHoursAdapter: WorkingHoursAdapter
    lateinit var mapFragment: SupportMapFragment
    lateinit var map: GoogleMap
    lateinit var mWaitDialog: AppWaitDialog
    var sldt = ""
    var selectedDate = ""
    var selectedTime = ""
    var res_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_resdetails, container, false)
        res_id = arguments?.getString("res_id").toString()
        sldt = arguments?.getString("sldt").toString()
        selectedDate = arguments?.getString("selectedDate").toString()
        selectedTime = arguments?.getString("selectedTime").toString()
        Log.d("selectedDate","1. ${selectedDate} ${selectedTime}")
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        EshtblshmntViewModel.userisinqueueData.value = null
        EshtblshmntViewModel.resmenuitemsData.value = null
        initViews()

//        val abc = DateToStringConversion.compareTwoTimes()

        eshtblshmntViewModel.getEshtblshmntDtls(requireContext(), res_id)
//        eshtblshmntViewModel.getRestroMenuitems(requireContext(), res_id)

        EshtblshmntViewModel.estbhlshmntDtlsData.observe(viewLifecycleOwner) {

            initSlider()
            tv_restaurant_name.text = it.res_name
//            tv_restaurant_description.text = it.description
            tv_restaurant_description.setResizableText(it.description, 2, true)
            tv_cusines.text = it.cuisine_type
            tv_seating.text = it.seating_options
            tv_restaurant_parking.text = it.parking
            mapFragment.getMapAsync(this)
            tv_second_res_name.text = it.res_name
            tv_rest_location.text = it.res_name

            if(it.book_now_btn.equals("0"))
            {
                this.lyt_book_now.visibility = View.GONE
            }
            else
            {
                this.lyt_book_now.visibility = View.VISIBLE
            }

            if(it.queue_btn.equals("0"))
            {
                this.lyt_join_queue.visibility = View.GONE
            }
            else
            {
                this.lyt_join_queue.visibility = View.VISIBLE
            }

            tv_restaurant_open_status.text = it.openStatus
            tv_restaurant_close_status.text = it.closedAt.replace("am", "AM").replace("pm", "PM")
            EshtblshmntViewModel.saveStatus.value = it.is_saved

        }

        EshtblshmntViewModel.resmenuitemsData.observe(viewLifecycleOwner) {
            it?.let {
                initMenuitemsRecyclerView()
            }
        }

        EshtblshmntViewModel.saveStatus.observe(viewLifecycleOwner) {
            if (it.equals("0", ignoreCase = true)) {
                img_save.setImageResource(R.drawable.save_yellow)
            } else {
                img_save.setImageResource(R.drawable.save_icon)
            }
        }

        EshtblshmntViewModel.userisinqueueData.observe(viewLifecycleOwner)
        {
            EshtblshmntViewModel.userisinqueueData.value?.let {

                when(it.user_is_in_queue)
                {
                    "1" -> {
                        val bundle = Bundle()
                        bundle.putString("mode","user_in_queue")
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

    fun initViews() {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        toolbar_resDtls = root.findViewById(R.id.toolbar_resDtls)
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        bottom_sheet = requireActivity().findViewById(R.id.bottom_sheet)
        iv_back = root.findViewById(R.id.iv_back)
        mBehavior = BottomSheetBehavior.from(bottom_sheet)
        mapFragment = (childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment?)!!

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

        vp_restaurant_details = root.findViewById(R.id.vp_restaurant_details)
        vp_restaurant_menu = root.findViewById(R.id.vp_restaurant_menu)
        lyt_book_now = root.findViewById(R.id.lyt_book_now)
        lyt_join_queue = root.findViewById(R.id.lyt_join_queue)
        notification_bell = root.findViewById(R.id.notification_bell)
        img_save = root.findViewById(R.id.img_save)
        tab_layout = root.findViewById(R.id.tab_layout)
        tab_1 = tab_layout.getTabAt(0)!!
        tab_2 = tab_layout.getTabAt(1)!!
        vp_restaurant_banner = root.findViewById(R.id.vp_restaurant_banner)
        dots_indicator = root.findViewById(R.id.dots_indicator)
        tv_restaurant_name = root.findViewById(R.id.tv_restaurant_name)
        tv_restaurant_description = root.findViewById(R.id.tv_restaurant_description)
        tv_cusines = root.findViewById(R.id.tv_cusines)
        tv_seating = root.findViewById(R.id.tv_seating)
        tv_second_res_name = root.findViewById(R.id.tv_second_res_name)
        tv_rest_location = root.findViewById(R.id.tv_rest_location)
        lyt_restaurant_close_status = root.findViewById(R.id.lyt_restaurant_close_status)
        tv_restaurant_open_status = root.findViewById(R.id.tv_restaurant_open_status)
        tv_restaurant_close_status = root.findViewById(R.id.tv_restaurant_close_status)
        tv_restaurant_parking = root.findViewById(R.id.tv_restaurant_parking)
        parentRecyclerViewItem = root.findViewById(R.id.parent_recyclerview)

        lyt_restaurant_close_status.setOnClickListener {
            showWorkingHoursListDialog()
        }

        lyt_book_now.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())

            if (AppPreferenceStorage.getUserid().equals("")) {
                AppCommonUtils.showCancelBookingDialog(requireActivity())
            } else {
                val bundle = Bundle()
                bundle.putString(
                    "restro_id",
                    EshtblshmntViewModel.estbhlshmntDtlsData.value!!.res_id
                )
                bundle.putString(
                    "restro_name",
                    EshtblshmntViewModel.estbhlshmntDtlsData.value!!.res_name
                )
                bundle.putString(
                    "restro_address",
                    EshtblshmntViewModel.estbhlshmntDtlsData.value!!.address
                )
                bundle.putString("sldt", sldt)
                bundle.putString("selectedDate", selectedDate)
                bundle.putString("selectedTime", selectedTime)
                val reservationFragment = ReservationFragment()
                reservationFragment.arguments = bundle
                AppCommonUtils.loadFragment(requireActivity(), reservationFragment)
            }
        }

        lyt_join_queue.setOnClickListener {

            if (AppPreferenceStorage.getUserid().equals("")) {
                AppCommonUtils.showCancelBookingDialog(requireActivity())
            } else {
                eshtblshmntViewModel.checkUserInQueue(requireContext(), EshtblshmntViewModel.estbhlshmntDtlsData.value!!.res_id)
            }
        }

        notification_bell.setOnClickListener {

            if (AppPreferenceStorage.getUserid().equals("")) {
                AppCommonUtils.showCancelBookingDialog(requireActivity())
            } else {
                if (EshtblshmntViewModel.saveStatus.value!!.equals("0")) {
                    eshtblshmntViewModel.saveEshtb(
                        requireContext(),
                        EshtblshmntViewModel.estbhlshmntDtlsData.value!!.res_id
                    )
                } else {
                    eshtblshmntViewModel.unSaveEshtb(
                        requireContext(),
                        EshtblshmntViewModel.estbhlshmntDtlsData.value!!.res_id
                    )
                }
            }

        }

        tab_layout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab_layout.selectedTabPosition) {
                    0 -> {
                        vp_restaurant_details.visibility = View.VISIBLE
                        vp_restaurant_menu.visibility = View.GONE
                    }
                    1 -> {
                        vp_restaurant_details.visibility = View.GONE
                        eshtblshmntViewModel.getRestroMenuitems(requireContext(), res_id)
                        vp_restaurant_menu.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        iv_back.setOnClickListener {
            onBackPressed()
        }
    }

    fun initSlider() {
        vp_restaurant_banner.adapter = RestaurantImageSliderAdapter(
            requireContext(),
            vp_restaurant_banner,
            EshtblshmntViewModel.estbhlshmntDtlsData.value!!.imageList
        )
        dots_indicator.attachTo(vp_restaurant_banner)
//        setupPagerIndidcatorDots()

//        dots_indicator.addDot()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initMenuitemsRecyclerView() {

        val parentItemAdapter = ParentItemAdapter(
            requireContext(),
            EshtblshmntViewModel.resmenuitemsData.value!!
        )

        parentRecyclerViewItem.smoothScrollToPosition(0)
        parentRecyclerViewItem
            .setLayoutManager(
                LinearLayoutManager(
                    requireActivity()
                )
            )
        parentRecyclerViewItem
            .setAdapter(parentItemAdapter)

        parentItemAdapter.notifyDataSetChanged()
    }


    fun onBackPressed() {
        val maneger: FragmentManager = requireActivity().supportFragmentManager
        maneger.beginTransaction()
            .remove(this)
            .commit()
        maneger.popBackStack()

        AppCommonUtils.hideKeyboard(requireActivity())

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("checkMap", "onMapReady called...")

        map = googleMap
//        val city_name = ExpertsViewModel.viewexprtdata.value!!.city

        val lat = EshtblshmntViewModel.estbhlshmntDtlsData.value!!.latitude
        val lng = EshtblshmntViewModel.estbhlshmntDtlsData.value!!.longitude

        val coordinates = LatLng(lat.toDouble(), lng.toDouble())
//      val coordinates = LatLng(21.146633, 79.088860)

        map.addMarker(
            MarkerOptions()
                .position(coordinates)
                .snippet("snippet")
                .title("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.res_marker_md))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20F))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showWorkingHoursListDialog() {

        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        val inflate: View =
            layoutInflater.inflate(R.layout.lyt_working_hrs, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog
        bottomSheetDialog.setContentView(inflate)

        val btn_close: TextView = inflate.findViewById(R.id.btn_close)
        val working_hrs_recy: RecyclerView = inflate.findViewById(R.id.working_hrs_recy)

        workingHoursAdapter = WorkingHoursAdapter(
            requireActivity(),
            requireContext(),
            EshtblshmntViewModel.estbhlshmntDtlsData.value!!.workinghrsList
        )

        //expertListRecyclerView.setHasFixedSize(true)
        working_hrs_recy.smoothScrollToPosition(0)
        working_hrs_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext()
            )
        )
        working_hrs_recy.setAdapter(workingHoursAdapter)
        workingHoursAdapter.notifyDataSetChanged()

        inflate.findViewById<TextView>(R.id.btn_close).setOnClickListener {
            this.mBottomSheetDialog.dismiss()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }


}
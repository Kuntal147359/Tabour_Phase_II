package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tabour.hospitality.BuildConfig
import com.tabour.hospitality.R
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.adapters.*
import com.tabour.hospitality.adapters.CategoryAdapter.OnListItemClick
import com.tabour.hospitality.base.MyApplicationClass
import com.tabour.hospitality.models.RVDates
import com.tabour.hospitality.utils.*
import com.tabour.hospitality.utils.AppCommonUtils.Companion.addresses
import com.tabour.hospitality.utils.AppCommonUtils.Companion.geocoder
import com.tabour.hospitality.utils.DateToStringConversion.Companion.getCurrentMonth
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SharedViewModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.android.synthetic.main.activity_main.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment(), ItemClickListener {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottom_sheet: View
    lateinit var menu: Menu
    lateinit var menuItem: MenuItem
    lateinit var content_view: LinearLayout
    lateinit var tv_username: TextView
    lateinit var txt_location: TextView
    lateinit var guest_count: TextView
    lateinit var booking_time: TextView
    lateinit var tv_restarnt: TextView
    lateinit var tv_cafe: TextView
    lateinit var txt_nearBy: TextView
    lateinit var et_location: TextView
    lateinit var img_notif_unread_status: ImageView
    lateinit var tv_notif_counter: TextView
    lateinit var img_restarnt: ImageView
    lateinit var img_cafe: ImageView
    lateinit var categories_recy: RecyclerView
//    lateinit var bookingslist_recy: RecyclerView
    lateinit var bookingslist_recy: ViewPager2
    lateinit var allrestaurants_recy: RecyclerView
    lateinit var available_now_recy: RecyclerView
    lateinit var nearby_restaurants_recy: RecyclerView
    lateinit var dots_indicator: DotsIndicator
    lateinit var loc_clear_txt: AppCompatButton
    lateinit var categories_progressbar: ProgressBar
    lateinit var explore_progressbar: ProgressBar
    lateinit var upcomingeventslist_progressbar: ProgressBar
    lateinit var available_now_progressbar: ProgressBar
    lateinit var nearby_progressbar: ProgressBar
    lateinit var lyt_search_availability: LinearLayout
    lateinit var lyt_restaurants: LinearLayout
    lateinit var lyt_cafe: LinearLayout
    lateinit var lyt_categories: LinearLayout
    lateinit var lyt_current_loc: LinearLayout
    lateinit var btn_available_view_more: LinearLayout
    lateinit var btn_nearby_view_more: LinearLayout
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var exploreResAdapter: ExploreResAdapter
    lateinit var availableNowAdapter: AvailableNowAdapter
    lateinit var upcomingEventsAdapter: UpcomingEventsAdapter
    lateinit var nearbyResAdapter: NearbyResAdapter
    lateinit var homeViewModel: HomeViewModel
    lateinit var sharedViewModel: SharedViewModel
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var bottomNav: BottomNavigationView
    lateinit var mBottomSheetDialog: BottomSheetDialog
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    lateinit var builder: LocationSettingsRequest.Builder
    lateinit var result: Task<LocationSettingsResponse>
    lateinit var mBehavior: BottomSheetBehavior<View>

    lateinit var currDate: String
    lateinit var currdaysArr: ArrayList<RVDates>
    var lattitude: Double = 0.0
    var longitude: Double = 0.0
    var currlattitude: Double = 0.0
    var currlongitude: Double = 0.0
    var restro_type = "1"
    var selectedDate = ""
    var currMonth = ""
    var sldt = ""
    var dsply_date = ""
    var selectedTime = ""
    var dsply_tm = ""
    var currLoc = ""
    var AUTOCOMPLETE_REQUEST_CODE: Int = 1

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    lateinit var mLocationRequest: LocationRequest

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected var mLocationSettingsRequest: LocationSettingsRequest? = null

    protected val REQUEST_CHECK_SETTINGS = 0x1
    val UPDATE_INTERVAL_IN_MILLISECONDS = 10000
    val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = (UPDATE_INTERVAL_IN_MILLISECONDS / 2).toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)

        Log.d("checkplayerId", "player id is: ${AppPreferenceStorage.getPlayerid()}")
        Log.e("OneSignalExample", "HomeFragment is launched..")

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)

        HomeViewModel.notification_read_status.value = null
        HomeViewModel.notification_read_count.value = null
        HomeViewModel.upcomingEventsListData.value = null
        EshtblshmntViewModel.userisinqueueData.value = null

//      showEnableLocationSetting()
        initViews()

        removeAllHighlighters()

//      bottomNav.visibility = View.GONE

        if(!AppPreferenceStorage.getUserid().equals(""))
        {
            homeViewModel.getupcomingEvents(requireContext())
        }

        homeViewModel.getReadnotificationstatus(requireContext())
        homeViewModel.getCategories(requireContext())
        homeViewModel.getExploreRestaurants(
            requireContext(),
            lattitude,
            longitude,
            restro_type,
            selectedDate,
            selectedTime,
            cuisine_type = ""
        )

        homeViewModel.getavailablenowRestaurants(
            requireContext(),
            lattitude,
            longitude,
            restro_type,
            selectedDate,
            "",
            cuisine_type = ""
        )

        HomeViewModel.notification_read_status.observe(viewLifecycleOwner){
            it?.let {
                if(it.equals("1"))
                {
                    tv_notif_counter.visibility = View.VISIBLE
                }
                else{
                    tv_notif_counter.visibility = View.GONE
                }
            }
        }

        HomeViewModel.notification_read_count.observe(viewLifecycleOwner){
            it?.let {
                tv_notif_counter.text = it
            }
        }


        HomeViewModel.upcomingEventsListData.observe(viewLifecycleOwner)
        {

            HomeViewModel.upcomingEventsListData.value?.let {
                Log.d("checkupcmevnts", HomeViewModel.upcomingEventsListData.value!!.size.toString())
                if(HomeViewModel.upcomingEventsListData.value!!.size > 0)
                {
                    initUpcomingEventsRecyclerView()
                }
                else{
                    bookingslist_recy.visibility = View.GONE
//                upcomingeventslist_progressbar.visibility = View.GONE
                }
            }
        }

        HomeViewModel.upcmgevnts_progressbar.observe(viewLifecycleOwner) {
            if (it) {
                upcomingeventslist_progressbar.visibility = View.VISIBLE
            } else {
                upcomingeventslist_progressbar.visibility = View.GONE
            }
        }

        HomeViewModel.res_categories.observe(viewLifecycleOwner) {
            initRecyclerView()
        }

        HomeViewModel.cat_progressbar.observe(viewLifecycleOwner) {
            if (it) {
                categories_progressbar.visibility = View.VISIBLE
            } else {
                categories_progressbar.visibility = View.GONE
            }
        }

        HomeViewModel.explrRestaurantListData.observe(viewLifecycleOwner)
        {
            initExploreResRecyclerView()
        }

        HomeViewModel.explr_progressbar.observe(viewLifecycleOwner) {
            if (it) {
                explore_progressbar.visibility = View.VISIBLE
            } else {
                explore_progressbar.visibility = View.GONE
            }
        }

        HomeViewModel.availnowResListData.observe(viewLifecycleOwner)
        {
            initAvailNowResRecyclerView()
        }

        HomeViewModel.availnow_progressbar.observe(viewLifecycleOwner) {
            if (it) {
                available_now_progressbar.visibility = View.VISIBLE
            } else {
                available_now_progressbar.visibility = View.GONE
            }
        }

        HomeViewModel.nearbyRestaurantListData.observe(viewLifecycleOwner)
        {
            initNearbyResRecyclerView()
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
//                if(it.user_is_in_queue.equals("1"))
//                {
//                    val bundle = Bundle()
//                    bundle.putString("mode","user_in_queue")
//                    bundle.putString("restro_id",it.res_id)
//                    bundle.putString("dinning_area_id","")
//                    bundle.putString("user_queue_id",it.user_queue_id)
//                    val viewQueueBooking = ViewQueueBooking()
//                    viewQueueBooking.arguments = bundle
//                    AppCommonUtils.loadFragment(requireActivity(),viewQueueBooking)
//                }
//                else{
//                    val bundle = Bundle()
//                    bundle.putString("restro_id",it.res_id)
//                    val chooseseatingQueueFragment = ChooseseatingQueueFragment()
//                    chooseseatingQueueFragment.arguments = bundle
//                    AppCommonUtils.loadFragment(requireActivity(),chooseseatingQueueFragment)
//                }
            }
        }

        HomeViewModel.nrby_progressbar.observe(viewLifecycleOwner) {
            if (it) {
                nearby_progressbar.visibility = View.VISIBLE
            } else {
                nearby_progressbar.visibility = View.GONE
            }
        }

        HomeViewModel.location_name.observe(viewLifecycleOwner) {
            currLoc = it.location_name
            txt_location.text = it.location_name
            currlattitude = it.currLat
            currlongitude = it.currLng

            homeViewModel.getNearbyRestaurants(
                requireContext(),
                currlattitude,
                currlongitude,
                restro_type,
                selectedDate,
                selectedTime,
                cuisine_type = ""
            )
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val place: Place = Autocomplete.getPlaceFromIntent(result.data!!)
                    val latlng: LatLng? = place.latLng

                    lattitude = latlng!!.latitude
                    longitude = latlng.longitude

                    getPlaceFromLatLng("", lattitude, longitude)
                    Log.d("checkLatlng", "Lattitude is $lattitude and Longitude is $longitude")
                }
            }


        return root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun initViews() {
        content_view = requireActivity().findViewById(R.id.main_content)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        tv_notif_counter = requireActivity().findViewById(R.id.tv_notif_counter)
        bottom_sheet = requireActivity().findViewById(R.id.bottom_sheet)
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.VISIBLE
        tv_username = root.findViewById(R.id.tv_username)
        guest_count = root.findViewById(R.id.guest_count)
        tv_restarnt = root.findViewById(R.id.tv_restarnt)
        tv_cafe = root.findViewById(R.id.tv_cafe)
        txt_nearBy = root.findViewById(R.id.txt_nearBy)
        booking_time = root.findViewById(R.id.booking_time)
        txt_location = root.findViewById(R.id.txt_location)
        img_restarnt = root.findViewById(R.id.img_restarnt)
        img_cafe = root.findViewById(R.id.img_cafe)
        categories_recy = root.findViewById(R.id.categories_recy)
        allrestaurants_recy = root.findViewById(R.id.allrestaurants_recy)
        bookingslist_recy = root.findViewById(R.id.bookingslist_recy)
        available_now_recy = root.findViewById(R.id.available_now_recy)
        nearby_restaurants_recy = root.findViewById(R.id.nearby_restaurants_recy)
        dots_indicator = root.findViewById(R.id.dots_indicator)
        lyt_search_availability = root.findViewById(R.id.lyt_search_availability)
        lyt_restaurants = root.findViewById(R.id.lyt_restaurants)
        lyt_cafe = root.findViewById(R.id.lyt_cafe)
        lyt_categories = root.findViewById(R.id.lyt_categories)
        btn_available_view_more = root.findViewById(R.id.btn_available_view_more)
        btn_nearby_view_more = root.findViewById(R.id.btn_nearby_view_more)
//        init_search = root.findViewById(R.id.init_search)
        categories_progressbar = root.findViewById(R.id.categories_progressbar)
        upcomingeventslist_progressbar = root.findViewById(R.id.upcomingeventslist_progressbar)
        explore_progressbar = root.findViewById(R.id.explore_progressbar)
        available_now_progressbar = root.findViewById(R.id.available_now_progressbar)
        nearby_progressbar = root.findViewById(R.id.nearby_progressbar)
        mBehavior = BottomSheetBehavior.from(bottom_sheet)

        menu = bottomNav.getMenu()
        menuItem = menu.getItem(0)
        menuItem.setChecked(true)

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

//        AppCommonUtils.hideKeyboard(requireActivity())
//        bottomNav.selectedItemId = R.id.nav_home

        currMonth = "${DateToStringConversion.getCurrentMonth().first} ${DateToStringConversion.getCurrentMonth().second}"
        currdaysArr = DateToStringConversion.getAllWeekDays()
        dsply_date = currdaysArr.get(1).display_date
        selectedDate = currdaysArr.get(1).param_date
        dsply_tm = DateToStringConversion.getCurrTimeinAmPm().first.replace("am","AM").replace("pm","PM")
        selectedTime = DateToStringConversion.getCurrTimeinAmPm().second
        booking_time.text = "${dsply_date} at ${dsply_tm}"

        Log.d("checkcurr", "${dsply_date} at ${dsply_tm}")

        if(AppPreferenceStorage.getCount().equals("",ignoreCase = true))
        {
            guest_count.text = "1"
            AppPreferenceStorage.saveCount("1")
        }
        else
        {
            guest_count.text = AppPreferenceStorage.getCount()
        }

        if(AppPreferenceStorage.getUsername().equals(""))
        {
            tv_username.text = String.format(resources.getString(R.string.hi_user), resources.getString(R.string.guest_user))
        }
        else{
            tv_username.text = String.format(resources.getString(R.string.hi_user), AppPreferenceStorage.getUsername())
        }

        lyt_search_availability.setOnClickListener {
            showBottomSheetDialog()
        }

        lyt_restaurants.setOnClickListener {
            restro_type = "1"
            lyt_categories.visibility = View.VISIBLE
            txt_nearBy.text = String.format(getString(R.string.text_nearby), "Restaurants")

            homeViewModel.getavailablenowRestaurants(
                requireContext(),
                lattitude,
                longitude,
                restro_type,
                selectedDate,
                selectedTime,
                cuisine_type = ""
            )

            homeViewModel.getNearbyRestaurants(
                requireContext(),
                currlattitude,
                currlongitude,
                restro_type,
                selectedDate,
                selectedTime,
                cuisine_type = ""
            )

            lyt_restaurants.background = requireContext().getDrawable(R.drawable.restaurent_bg)
//            img_cafe.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)
            img_restarnt.setColorFilter(requireContext().getColor(R.color.white))
            tv_restarnt.setTextColor(requireContext().getColor(R.color.white))

            lyt_cafe.background = requireContext().getDrawable(R.drawable.cafes_bg)
            img_cafe.setColorFilter(resources.getColor(R.color.faint_primary_color))
            tv_cafe.setTextColor(requireContext().getColor(R.color.faint_primary_color))
        }

        btn_available_view_more.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(),SearchRestauants())
        }

        btn_nearby_view_more.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(),SearchRestauants())
        }

        lyt_cafe.setOnClickListener {
            restro_type = "2"
            lyt_categories.visibility = View.GONE
            txt_nearBy.text = String.format(getString(R.string.text_nearby), "Cafes")

            homeViewModel.getavailablenowRestaurants(
                requireContext(),
                lattitude,
                longitude,
                restro_type,
                selectedDate,
                selectedTime,
                cuisine_type = ""
            )

            homeViewModel.getNearbyRestaurants(
                requireContext(),
                currlattitude,
                currlongitude,
                restro_type,
                selectedDate,
                selectedTime,
                cuisine_type = ""
            )

            lyt_cafe.background = requireContext().getDrawable(R.drawable.restaurent_bg)
//            img_cafe.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)
            img_cafe.setColorFilter(resources.getColor(R.color.white))
            tv_cafe.setTextColor(requireContext().getColor(R.color.white))

            lyt_restaurants.background = requireContext().getDrawable(R.drawable.cafes_bg)
            img_restarnt.setColorFilter(resources.getColor(R.color.faint_primary_color))
            tv_restarnt.setTextColor(requireContext().getColor(R.color.faint_primary_color))

        }

//        init_search.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("restro_type", restro_type)
//
//            val searchRestauants = SearchRestauants()
//            searchRestauants.arguments = bundle
//            AppCommonUtils.loadFragment(requireActivity(), searchRestauants)
//        }

        // row click listener
        categories_recy.addOnItemTouchListener(
            RecyclerTouchListener(
                requireContext(),
                categories_recy,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val categories = HomeViewModel.res_categories.value!!.get(position)

                        homeViewModel.getExploreRestaurants(
                            requireContext(),
                            lattitude,
                            longitude,
                            restro_type,
                            selectedDate,
                            selectedTime,
                            cuisine_type = categories.id.toString()
                        )
                        homeViewModel.getNearbyRestaurants(
                            requireContext(),
                            currlattitude,
                            currlongitude,
                            restro_type,
                            selectedDate,
                            selectedTime,
                            cuisine_type = categories.id.toString()
                        )

                    }

                    override fun onLongClick(view: View?, position: Int) {
                        TODO("Not yet implemented")
                    }

                })
        )

    }

    @SuppressLint("SetTextI18n")
    fun showBottomSheetDialog() {
        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }

//        if (mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
//            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//        } else {
//            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//        }

        val inflate: View =
            layoutInflater.inflate(R.layout.fragment_search_availability, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog
        bottomSheetDialog.setContentView(inflate)

        val tab_layout: TabLayout = inflate.findViewById(R.id.tab_layout)
        val lyt_guest: ConstraintLayout = inflate.findViewById(R.id.lyt_guest)
        val lyt_location: LinearLayout = inflate.findViewById(R.id.lyt_location)
        val rvDate: RecyclerView = inflate.findViewById(R.id.rvDate)
        val rvTime: RecyclerView = inflate.findViewById(R.id.rvTime)
        et_location = inflate.findViewById(R.id.et_location)
        loc_clear_txt = inflate.findViewById(R.id.loc_clear_txt)
        lyt_current_loc = inflate.findViewById(R.id.lyt_current_loc)

        val btnDone: AppCompatTextView = inflate.findViewById(R.id.btnDone)
        var guestCount = ""
        val tvSearchGuestsCount: AppCompatTextView = inflate.findViewById(R.id.tvSearchGuestsCount)
        SharedViewModel.currlocation_name.value = null

        tab_layout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab!!.position) {
                    0 -> {
                        lyt_guest.visibility = View.VISIBLE
                        lyt_location.visibility = View.GONE
                    }
                    1 -> {
                        lyt_guest.visibility = View.GONE
                        lyt_location.visibility = View.VISIBLE
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        et_location.setOnClickListener {

            if (!Places.isInitialized()) {
                Places.initialize(
                    requireContext(),
                    BuildConfig.API_KEY
                )
            }

            val fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.TYPES
            )

            val intent: Intent = Autocomplete
                .IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields
                ).setTypeFilter(TypeFilter.CITIES)
                .build(MyApplicationClass.getInstance())
            resultLauncher.launch(intent)

        }

        SharedViewModel.currlocation_name.observe(viewLifecycleOwner){
            it?.let {
                lattitude = it.currLat
                longitude = it.currLng
                getPlaceFromLatLng("", lattitude, longitude)
            }
        }

        lyt_current_loc.setOnClickListener {
//            this.mBottomSheetDialog.dismiss()
            (activity as MainActivity).getLocation()
        }

        loc_clear_txt.setOnClickListener {
            et_location.text = ""
            loc_clear_txt.visibility = View.GONE
            lattitude = 0.0
            longitude = 0.0
        }

        currDate = DateToStringConversion.getCurrDate()

        // Days array
        val daysArr = DateToStringConversion.getAllWeekDays()
        selectedDate = daysArr.get(1).param_date
        dsply_date = daysArr.get(1).display_date

        // Time array
        var timeArr = DateToStringConversion.getAllTimeIntervals(dsply_date, currdaysArr.get(1).display_date)
//        val timeArr = DateToStringConversion.getAllTimeIntervals(dsply_date,"")
        selectedTime = timeArr.get(1).param_time
        dsply_tm = timeArr.get(1).display_time

        val rvDateAdapter = RVDateAdapter(
            requireContext(),
            daysArr
        )

        rvDate.smoothScrollToPosition(0)
        rvDate.setLayoutManager(
            LinearLayoutManager(requireContext())
        )

        rvDate.setAdapter(rvDateAdapter)
//        rvDate.notifyDataSetChanged()

        rvDate.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            var scrollDy = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState === RecyclerView.SCROLL_STATE_IDLE) {
                    val position: Int = (rvDate.getLayoutManager() as LinearLayoutManager)
                        .findFirstVisibleItemPosition()

                    rvDate.smoothScrollToPosition(position)
                    Log.d("checkScroll", "${position.toString()}")

//                    selectedDate = daysArr.get(position + 1).display_date
                    selectedDate = daysArr.get(position + 1).param_date
                    dsply_date = daysArr.get(position + 1).display_date


                    if(position == 0)
                    {
                        timeArr = DateToStringConversion.getAllTimeIntervals(dsply_date, currdaysArr.get(1).display_date)
                        val rvTimeAdapter = RVTimeAdapter(
                            requireContext(),
                            timeArr
                        )

                        rvTime.smoothScrollToPosition(0)
                        rvTime.layoutManager = LinearLayoutManager(requireContext())
                        rvTime.adapter = rvTimeAdapter

                        selectedTime = timeArr.get(position + 1).param_time
                        dsply_tm = timeArr.get(position + 1).display_time

                    }
                    else
                    {
                        timeArr = DateToStringConversion.getAllTimeIntervals(dsply_date,"")
                        val rvTimeAdapter = RVTimeAdapter(
                            requireContext(),
                            timeArr
                        )

                        rvTime.smoothScrollToPosition(0)
                        rvTime.layoutManager = LinearLayoutManager(requireContext())
                        rvTime.adapter = rvTimeAdapter

                        selectedTime = timeArr.get(1).param_time
                        dsply_tm = timeArr.get(1).display_time

                    }

//                    Log.d("checkScroll", "Display date is ${selectedDate} and param is ${paramDate}")

                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollDy += dy
            }
        })

        val rvTimeAdapter = RVTimeAdapter(
            requireContext(),
            timeArr
        )

        rvTime.smoothScrollToPosition(0)
        rvTime.layoutManager = LinearLayoutManager(requireContext())
        rvTime.adapter = rvTimeAdapter

        rvTime.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            var scrollDy = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState === RecyclerView.SCROLL_STATE_IDLE) {
                    val position: Int = (rvTime.getLayoutManager() as LinearLayoutManager)
                        .findFirstVisibleItemPosition()

                    rvTime.smoothScrollToPosition(position)
                    selectedTime = timeArr.get(position + 1).param_time
                    dsply_tm = timeArr.get(position + 1).display_time

                    Log.d("checkScroll", selectedTime)
                    Log.d("checkScrollvalue", selectedTime)

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollDy += dy

            }
        })

//        inflate.findViewById<AppCompatImageView>(R.id.btnClose).setOnClickListener {
//            this.mBottomSheetDialog.dismiss()
//        }

        inflate.findViewById<AppCompatImageView>(R.id.btnPlus).setOnClickListener {
            val currCount = tvSearchGuestsCount.text.toString()
            val finalCount = currCount.toInt() + 1

            if (finalCount.toInt() > 1) {
                inflate.findViewById<AppCompatImageView>(R.id.btnPlus).isEnabled = true
            }

            tvSearchGuestsCount.text = finalCount.toString()
        }

        inflate.findViewById<AppCompatImageView>(R.id.btnMinus).setOnClickListener {
            val currCount = tvSearchGuestsCount.text.toString()
            var finalCount = -1
            if (currCount.toInt() > 1) {
                finalCount = currCount.toInt() - 1
            } else {
                finalCount = currCount.toInt()
                it.isEnabled = false
            }

            tvSearchGuestsCount.text = finalCount.toString()
        }

        btnDone.setOnClickListener {
            booking_time.text = "${dsply_date} at ${dsply_tm}"
            guest_count.text = tvSearchGuestsCount.text.toString()

            if(!et_location.text.toString().equals(""))
            {
                txt_location.text = et_location.text.toString()
            }

            if(tvSearchGuestsCount.text.toString().equals("1"))
            {
                AppPreferenceStorage.saveCount("1")
            }
            else{
                AppPreferenceStorage.saveCount(tvSearchGuestsCount.text.toString())
            }

            SharedViewModel.slctLatLng.value = LatLng(lattitude, longitude)

            homeViewModel.getavailablenowRestaurants(
                requireContext(),
                lattitude,
                longitude,
                restro_type,
                selectedDate,
                selectedTime,
                cuisine_type = ""
            )

            this.mBottomSheetDialog.dismiss()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }

    fun removeAllHighlighters()
    {
        requireActivity().lyt_my_bookings.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.transparent))
        requireActivity().lyt_contact_us.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.transparent))
        requireActivity().lyt_tabour_policy.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.transparent))
        requireActivity().lyt_termsncndtns.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.transparent))
        requireActivity().tv_my_bookings.setTextColor(ContextCompat.getColor(requireActivity(),R.color.white))
        requireActivity().tv_contact_us.setTextColor(ContextCompat.getColor(requireActivity(),R.color.white))
        requireActivity().tv_tabour_policy.setTextColor(ContextCompat.getColor(requireActivity(),R.color.white))
        requireActivity().tv_termsncndtns.setTextColor(ContextCompat.getColor(requireActivity(),R.color.white))
    }

    fun getPlaceFromLatLng(cityname: String, lat: Double, longitude: Double) {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(lat, longitude, 1) as List<Address>

        var city = ""
        if (cityname.equals("", ignoreCase = true)) {
            city = addresses[0].locality
        } else {
            city = cityname
        }

        val country = addresses[0].countryName

        Log.d("checkCity", country)

//        et_location.setText("${city}, ${country}")
        et_location.setText(city)
        loc_clear_txt.visibility = View.VISIBLE

    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS.toLong()

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    protected fun buildLocationSettingsRequest() {
        builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * [com.google.android.gms.location.SettingsApi.checkLocationSettings] method, with the results provided through a `PendingResult`.
     */
    protected fun checkLocationSettings() {

        result = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->

            when (task.result) {

            }
        }
    }

    // Modern approch
    fun showEnableLocationSetting() {
        activity?.let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val task = LocationServices.getSettingsClient(it)
                .checkLocationSettings(builder.build())

            task.addOnSuccessListener { response ->
                val states = response.locationSettingsStates
                if (states!!.isLocationPresent) {
                    //Do something
                }
            }
            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        // Handle result in onActivityResult()
//                        e.startResolutionForResult(it,
//                            this.REQUEST_CHECK_SETTINGS)

                        startIntentSenderForResult(
                            e.getResolution().getIntentSender(),
                            REQUEST_CHECK_SETTINGS,
                            null,
                            0,
                            0,
                            0,
                            null
                        )

                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

//    private val resolutionForResult =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
//            if (activityResult.resultCode == RESULT_OK)
//            //startLocationUpdates() or do whatever you want
//            else {
//                showMessage("we can't determine your location")
//            }
//        }

    @SuppressLint("NotifyDataSetChanged")
    fun initRecyclerView() {
        categoryAdapter = CategoryAdapter(
            requireActivity(),
            requireContext(),
            homeViewModel,
            HomeViewModel.res_categories.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        categories_recy.smoothScrollToPosition(0)
        categories_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        categories_recy.setAdapter(categoryAdapter)
        categoryAdapter.notifyDataSetChanged()

        val onListItemClick: OnListItemClick = object : OnListItemClick {
            override fun onClick(view: View?, position: Int) {
                // you will get click here
                // do your code here
                Log.d("checkClicked", position.toString())
                val categories = HomeViewModel.res_categories.value!!.get(position)

                homeViewModel.getavailablenowRestaurants(
                    requireContext(),
                    lattitude,
                    longitude,
                    restro_type,
                    selectedDate,
                    selectedTime,
                    cuisine_type = categories.id.toString()
                )

                homeViewModel.getNearbyRestaurants(
                    requireContext(),
                    currlattitude,
                    currlongitude,
                    restro_type,
                    selectedDate,
                    selectedTime,
                    cuisine_type = categories.id.toString()
                )

            }
        }

        categoryAdapter.setClickListener(onListItemClick)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initExploreResRecyclerView() {
        exploreResAdapter = ExploreResAdapter(
            requireActivity(),
            requireContext(),
            HomeViewModel.explrRestaurantListData.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        allrestaurants_recy.smoothScrollToPosition(0)
        allrestaurants_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        allrestaurants_recy.setAdapter(exploreResAdapter)
        exploreResAdapter.notifyDataSetChanged()
        exploreResAdapter.setClickListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initAvailNowResRecyclerView() {
        availableNowAdapter = AvailableNowAdapter(
            requireActivity(),
            requireContext(),
            HomeViewModel.availnowResListData.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        available_now_recy.smoothScrollToPosition(0)
        available_now_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        available_now_recy.setAdapter(availableNowAdapter)
        availableNowAdapter.notifyDataSetChanged()
        availableNowAdapter.setClickListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initUpcomingEventsRecyclerView() {
        bookingslist_recy.visibility = View.VISIBLE
        bookingslist_recy.adapter = UpcomingEventsAdapter(
            requireActivity(),
            requireContext(),
            bookingslist_recy,
            HomeViewModel.upcomingEventsListData.value!!
        )

//        bookingslist_recy.smoothScrollToPosition(0)
//        bookingslist_recy.setLayoutManager(
//            LinearLayoutManager(
//                requireContext(),
//                LinearLayoutManager.HORIZONTAL,
//                false
//            )
//        )
//        PagerSnapHelper().attachToRecyclerView(bookingslist_recy)
//        bookingslist_recy.setAdapter(upcomingEventsAdapter)
//        upcomingEventsAdapter.notifyDataSetChanged()

        dots_indicator.attachTo(bookingslist_recy)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initNearbyResRecyclerView() {
        nearbyResAdapter = NearbyResAdapter(
            requireActivity(),
            requireContext(),
            HomeViewModel.nearbyRestaurantListData.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        nearby_restaurants_recy.smoothScrollToPosition(0)
        nearby_restaurants_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        nearby_restaurants_recy.setAdapter(nearbyResAdapter)
        nearbyResAdapter.notifyDataSetChanged()
        nearbyResAdapter.setClickListener(this)
    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        requireActivity().finish()
    }

    override fun onClick(view: View?, module: String, position: Int) {
        when(module)
        {
            "Book_now_explore" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    Log.d("checkseldate",selectedDate)
                    sldt = "${DateToStringConversion.getYearMonthFromDate(selectedDate).first}, ${DateToStringConversion.getYearMonthFromDate(selectedDate).second}"
                    Log.d("checkseldate",sldt)
                    val bundle = Bundle()
                    bundle.putString("restro_id", HomeViewModel.explrRestaurantListData.value!!.get(position).id)
                    bundle.putString("restro_name", HomeViewModel.explrRestaurantListData.value!!.get(position).name)
                    bundle.putString("restro_address", HomeViewModel.explrRestaurantListData.value!!.get(position).address)
                    bundle.putString("sldt", sldt)
                    bundle.putString("selectedDate", dsply_date)
                    bundle.putString("selectedTime", dsply_tm)
                    val reservationFragment = ReservationFragment()
                    reservationFragment.arguments = bundle
                    AppCommonUtils.loadFragment(requireActivity(), reservationFragment)
                }
            }
            "Book_now_available" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else
                {
                    sldt = "${DateToStringConversion.getYearMonthFromDate(selectedDate).first}, ${DateToStringConversion.getYearMonthFromDate(selectedDate).second}"
                    val bundle = Bundle()
                    bundle.putString("restro_id", HomeViewModel.availnowResListData.value!!.get(position).id.toString())
                    bundle.putString("restro_name", HomeViewModel.availnowResListData.value!!.get(position).name.toString())
                    bundle.putString("restro_address", HomeViewModel.availnowResListData.value!!.get(position).address.toString())
                    bundle.putString("sldt", sldt)
                    bundle.putString("selectedDate", dsply_date)
                    bundle.putString("selectedTime", dsply_tm)
                    val reservationFragment = ReservationFragment()
                    reservationFragment.arguments = bundle
                    AppCommonUtils.loadFragment(requireActivity(), reservationFragment)
                }
            }
            "Book_now_nearby" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    sldt = "${DateToStringConversion.getYearMonthFromDate(selectedDate).first}, ${DateToStringConversion.getYearMonthFromDate(selectedDate).second}"
                    val bundle = Bundle()
                    bundle.putString("restro_id", HomeViewModel.nearbyRestaurantListData.value!!.get(position).id.toString())
                    bundle.putString("restro_name", HomeViewModel.nearbyRestaurantListData.value!!.get(position).name.toString())
                    bundle.putString("restro_address", HomeViewModel.nearbyRestaurantListData.value!!.get(position).address.toString())
                    bundle.putString("sldt", sldt)
                    bundle.putString("selectedDate", dsply_date)
                    bundle.putString("selectedTime", dsply_tm)
                    val reservationFragment = ReservationFragment()
                    reservationFragment.arguments = bundle
                    AppCommonUtils.loadFragment(requireActivity(), reservationFragment)
                }
            }
            "checkqueueexplore" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    eshtblshmntViewModel.checkUserInQueue(requireContext(), HomeViewModel.explrRestaurantListData.value!!.get(position).id.toString())
                }
            }
            "checkqueueAvail" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    eshtblshmntViewModel.checkUserInQueue(requireContext(), HomeViewModel.availnowResListData.value!!.get(position).id.toString())
                }
            }
            "checkqueue" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    eshtblshmntViewModel.checkUserInQueue(requireContext(), HomeViewModel.nearbyRestaurantListData.value!!.get(position).id.toString())
                }
            }
            "Exploreresdtls" -> {
                sldt = "${DateToStringConversion.getYearMonthFromDate(selectedDate).first}, ${DateToStringConversion.getYearMonthFromDate(selectedDate).second}"
                val bundle = Bundle()
                bundle.putString("res_id",HomeViewModel.explrRestaurantListData.value!!.get(position).id)
                bundle.putString("sldt", sldt)
                bundle.putString("selectedDate", dsply_date)
                bundle.putString("selectedTime", dsply_tm)
                val resdetailsFragment = ResdetailsFragment()
                resdetailsFragment.arguments = bundle
                AppCommonUtils.loadFragment(requireActivity(), resdetailsFragment)
            }
            "Availableresdtls" -> {
                sldt = "${DateToStringConversion.getYearMonthFromDate(selectedDate).first}, ${DateToStringConversion.getYearMonthFromDate(selectedDate).second}"
                val bundle = Bundle()
                bundle.putString("res_id",HomeViewModel.availnowResListData.value!!.get(position).id.toString())
                bundle.putString("sldt", sldt)
                bundle.putString("selectedDate", dsply_date)
                bundle.putString("selectedTime", dsply_tm)
                val resdetailsFragment = ResdetailsFragment()
                resdetailsFragment.arguments = bundle
                AppCommonUtils.loadFragment(requireActivity(), resdetailsFragment)
            }
            "NearByresdtls" -> {
                sldt = "${DateToStringConversion.getYearMonthFromDate(selectedDate).first}, ${DateToStringConversion.getYearMonthFromDate(selectedDate).second}"
                val bundle = Bundle()
                bundle.putString("res_id",HomeViewModel.nearbyRestaurantListData.value!!.get(position).id.toString())
                bundle.putString("sldt", sldt)
                bundle.putString("selectedDate", dsply_date)
                bundle.putString("selectedTime", dsply_tm)
                val resdetailsFragment = ResdetailsFragment()
                resdetailsFragment.arguments = bundle
                AppCommonUtils.loadFragment(requireActivity(), resdetailsFragment)
            }
        }
    }

}
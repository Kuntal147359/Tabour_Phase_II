package com.tabour.hospitality.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.tabour.hospitality.R
import com.tabour.hospitality.base.NetworkConnectionReceiver
import com.tabour.hospitality.fragments.*
import com.tabour.hospitality.models.CurrentLocation
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.BackdropViewAnimation
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.include_drawer_content.*
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var content_view: LinearLayout
    lateinit var secondary_back: LinearLayout
    lateinit var lyt_main: LinearLayout
    lateinit var main_page: LinearLayout
    lateinit var toolbar: Toolbar
    lateinit var sideNavigationView: LinearLayout
    lateinit var lyt_tabour_logo: LinearLayout
    lateinit var bottomNav: BottomNavigationView
    lateinit var nav_Menu: Menu
    lateinit var home_btn: LinearLayout
    lateinit var notification_bell: LinearLayout
    lateinit var tv_my_bookings: TextView
    lateinit var tv_contact_us: TextView
    lateinit var tv_tabour_policy: TextView
    lateinit var tv_termsncndtns: TextView
    lateinit var tv_logout: TextView
    lateinit var tv_login: TextView
    lateinit var tv_logout_deactivate: TextView
    lateinit var lyt_my_bookings: LinearLayout
    lateinit var lyt_membership: LinearLayout
    lateinit var lyt_contact_us: LinearLayout
    lateinit var lyt_tabour_policy: LinearLayout
    lateinit var lyt_termsncndtns: LinearLayout
    lateinit var lyt_login: LinearLayout
    lateinit var lyt_logout: LinearLayout
    lateinit var lyt_logout_deactivate: LinearLayout
    lateinit var backdropAnimation: BackdropViewAnimation
    lateinit var homeViewModel: HomeViewModel
    lateinit var sharedViewModel: SharedViewModel
    lateinit var tv_username: TextView
    lateinit var tv_mobile_number: TextView
    lateinit var imageViews_userprofile: ShapeableImageView

    protected val REQUEST_CHECK_SETTINGS = 0x1
    private val permissionId = 2
    var isHide: Boolean = true
    var curr_language: String = ""
    var mode: String = ""
    var category: String = ""
    var booking_id: String = ""
    lateinit var list: List<Address>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        AppCommonUtils.getCurrentActivity(this@MainActivity)

        // Notification handler

//        if(mode.equals("notification"))
//        {
//            category = intent.getStringExtra("category").toString()
//            if(category.equals("1"))
//            {
//                AppCommonUtils.loadFragment(this, NotificationFragment())
//            }
//        }

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        curr_language = Locale.getDefault().language
        HomeViewModel.loginStatus.value = true
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        HomeViewModel.activateStatus.value = ""

        initViews()
        getLocation()

        SharedViewModel.connectivityStatus.observe(this){
            if(!it)
            {
                AppCommonUtils.showNetworkConnectivityDialog(this)
            }
        }

        HomeViewModel.activateStatus.observe(this){

            if(!AppPreferenceStorage.getUserid().equals(""))
            {
                when(it)
                {
                    "0" -> {
                        homeViewModel.logout(this)
                    }
                    "1" -> {
                        //Todo not implemented
                    }
                    "" -> {
                        //Todo not implemented
                    }
                }
            }
        }
    }

    fun initViews() {
        sideNavigationView = findViewById(R.id.nav_view)
        bottomNav = findViewById(R.id.bottom_nav_view)
        content_view = findViewById(R.id.main_content)
        secondary_back = findViewById(R.id.secondary_back)
        lyt_main = findViewById(R.id.lyt_main)
        main_page = findViewById(R.id.main_page)
        toolbar = findViewById(R.id.toolbar)
        home_btn = toolbar.findViewById(R.id.home_btn)
        notification_bell = toolbar.findViewById(R.id.notification_bell)
        lyt_tabour_logo = findViewById(R.id.lyt_tabour_logo)

        // For side navigation
//        nav_Menu = sideNavigationView.menu

        tv_username = sideNavigationView.findViewById(R.id.tv_username)
        tv_mobile_number = sideNavigationView.findViewById(R.id.tv_mobile_number)
        imageViews_userprofile = sideNavigationView.findViewById(R.id.imageViews_userprofile)

        AppPreferenceStorage.getUsername().let {
            if(AppPreferenceStorage.getUsername().equals(""))
            {
                tv_username.text = getString(R.string.guest_user)
            }
            else{
                tv_username.text = "Hi, ${AppPreferenceStorage.getUsername()}"
            }
        }

        AppPreferenceStorage.getMobileNum().let {
            Log.d("checknumber", AppPreferenceStorage.getMobileNum())
            tv_mobile_number.text = AppPreferenceStorage.getMobileNum()
        }

        AppPreferenceStorage.getProfilePic().let {

            if(AppPreferenceStorage.getProfilePic().equals(""))
            {
                imageViews_userprofile.setImageDrawable(getDrawable(R.drawable.user_icon_filled))
            }
            else{
                Glide.with(this)
                    .load(AppPreferenceStorage.getProfilePic())
                    .into(imageViews_userprofile)
            }
        }

        lyt_tabour_logo.setOnClickListener(this)

        tv_my_bookings = findViewById(R.id.tv_my_bookings)
        tv_contact_us = findViewById(R.id.tv_contact_us)
        tv_tabour_policy = findViewById(R.id.tv_tabour_policy)
        tv_termsncndtns = findViewById(R.id.tv_termsncndtns)
        tv_logout = findViewById(R.id.tv_logout)
        tv_login = findViewById(R.id.tv_login)
        tv_logout_deactivate = findViewById(R.id.tv_logout_deactivate)

        lyt_my_bookings = sideNavigationView.findViewById(R.id.lyt_my_bookings)
        lyt_my_bookings.setOnClickListener(this)

//        lyt_membership = sideNavigationView.findViewById(R.id.lyt_membership)
//        lyt_membership.setOnClickListener(this)

        lyt_contact_us = sideNavigationView.findViewById(R.id.lyt_contact_us)
        lyt_contact_us.setOnClickListener(this)

        lyt_tabour_policy = sideNavigationView.findViewById(R.id.lyt_tabour_policy)
        lyt_tabour_policy.setOnClickListener(this)

        lyt_termsncndtns = sideNavigationView.findViewById(R.id.lyt_termsncndtns)
        lyt_termsncndtns.setOnClickListener(this)

        lyt_login = sideNavigationView.findViewById(R.id.lyt_login)
        lyt_login.setOnClickListener(this)

        lyt_logout = sideNavigationView.findViewById(R.id.lyt_logout)
        lyt_logout.setOnClickListener(this)

        lyt_logout_deactivate = sideNavigationView.findViewById(R.id.lyt_logout_deactivate)
        lyt_logout_deactivate.setOnClickListener(this)

        if(AppPreferenceStorage.getUserid().equals(""))
        {
            lyt_login.visibility = View.VISIBLE
            lyt_logout.visibility = View.GONE
            lyt_logout_deactivate.visibility = View.GONE
        }
        else
        {
            lyt_login.visibility = View.GONE
            lyt_logout.visibility = View.VISIBLE
            lyt_logout_deactivate.visibility = View.VISIBLE
        }

        hideMenu(content_view, curr_language)

        // For bottom navigation
        bottomNav.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    AppCommonUtils.loadFragment(this@MainActivity, HomeFragment())
                }
                R.id.nav_search -> {
                    AppCommonUtils.loadFragment(this@MainActivity, SearchRestauants())
                }
                R.id.nav_saved -> {
                    if(AppPreferenceStorage.getUserid().equals(""))
                    {
                        AppCommonUtils.showCancelBookingDialog(this@MainActivity)
                    }
                    else
                    {
                        AppCommonUtils.loadFragment(this@MainActivity, SavedRestaurants())
                    }
                }
                R.id.nav_Profile -> {
                    if(AppPreferenceStorage.getUserid().equals(""))
                    {
                        AppCommonUtils.showCancelBookingDialog(this@MainActivity)
                    }
                    else
                    {
                        AppCommonUtils.loadFragment(this@MainActivity, Profile())
                    }
                }
            }
            return@setOnItemSelectedListener true
        }

        home_btn.setOnClickListener {

            if (this.isHide) {
                showMenu(content_view, curr_language)
            } else {
                hideMenu(content_view, curr_language)
            }
        }

        notification_bell.setOnClickListener {

            if(AppPreferenceStorage.getUserid().equals(""))
            {
                AppCommonUtils.showCancelBookingDialog(this@MainActivity)
            }
            else
            {
                AppCommonUtils.loadFragment(this@MainActivity, NotificationFragment())
            }
        }

        HomeViewModel.loginStatus.observe(this) {
            Log.d(
                "checkloginparam",
                "login status in Main Activity is ${HomeViewModel.loginStatus.value}"
            )
            if (!it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        Log.e("OneSignalExample", "onCreate is called..")
//        if(mode.equals("notification"))
//        {
//            Log.e("OneSignalExample", "Notification fragment is called..")
//            category = intent.getStringExtra("category").toString()
//            booking_id = intent.getStringExtra("booking_id").toString()
//            when(category)
//            {
//                "1,2,5" ->{
//                    AppCommonUtils.loadFragment(this, NotificationFragment())
//                }
//                "3" ->{
//                    Log.e("OneSignalExample", "category value: 3")
//                    AppCommonUtils.hideKeyboard(this)
//
//                    val bundle = Bundle()
//                    bundle.putString("reservation_id",booking_id)
//                    bundle.putString("category", "1")
//                    val viewBooking = ViewBooking()
//                    viewBooking.arguments = bundle
//                    AppCommonUtils.loadFragment(this@MainActivity, viewBooking)
//                }
//                "4" -> {
//
//                    AppCommonUtils.hideKeyboard(this)
//
//                    val bundle = Bundle()
//                    bundle.putString("mode","user_in_queue")
//                    bundle.putString("category", "1")
//                    bundle.putString("user_queue_id",booking_id)
//                    val viewQueueBooking = ViewQueueBooking()
//                    viewQueueBooking.arguments = bundle
//                    AppCommonUtils.loadFragment(this@MainActivity, viewQueueBooking)
//
//                }
//            }
//        }
//        else{
//            Log.e("OneSignalExample", "HomeFragment fragment is called..")
//            AppCommonUtils.loadFragment(this@MainActivity, HomeFragment())
//        }

        Log.e("OneSignalExample", "onStart is called..")
        mode = intent.getStringExtra("mode").toString()
        Log.e("OneSignalExample", "mode is ${mode}")

        if(mode.equals("notification"))
        {
            Log.e("OneSignalExample", "Notification mode is called..")
            category = intent.getStringExtra("category").toString()
            booking_id = intent.getStringExtra("booking_id").toString()
            Log.e("OneSignalExample", "category is: ${category}")
            when(category)
            {
                "1" ->{
                    Log.e("OneSignalExample", "category value: ${category}")
                    AppCommonUtils.loadFragment(this, NotificationFragment())
                }
                "2" -> {
                    Log.e("OneSignalExample", "category value: ${category}")
                    AppCommonUtils.loadFragment(this, NotificationFragment())
                }
                "3" ->{
                    Log.e("OneSignalExample", "category value: 3")
                    AppCommonUtils.hideKeyboard(this)

                    val bundle = Bundle()
                    bundle.putString("reservation_id",booking_id)
                    bundle.putString("category", "1")
                    val viewBooking = ViewBooking()
                    viewBooking.arguments = bundle
                    AppCommonUtils.loadFragment(this@MainActivity, viewBooking)
                }
                "4" -> {

                    AppCommonUtils.hideKeyboard(this)

                    val bundle = Bundle()
                    bundle.putString("mode","user_in_queue")
                    bundle.putString("category", "1")
                    bundle.putString("user_queue_id",booking_id)
                    val viewQueueBooking = ViewQueueBooking()
                    viewQueueBooking.arguments = bundle
                    AppCommonUtils.loadFragment(this@MainActivity, viewQueueBooking)

                }
                "5" -> {
                    Log.e("OneSignalExample", "category value: ${category}")
                    AppCommonUtils.loadFragment(this, NotificationFragment())
                }
            }
        }
        else{
            Log.e("OneSignalExample", "HomeFragment fragment is called..")
            AppCommonUtils.loadFragment(this@MainActivity, HomeFragment())
        }


    }

    fun showMenu(view: View, curr_language: String): ObjectAnimator? {
        isHide = false

        if (curr_language.equals("en", ignoreCase = true)) {
            view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .translationX(view.width.toFloat() / 2.0f)
                .setDuration(300)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animator: Animator) {
                            super.onAnimationStart(animator)

                            secondary_back.visibility = View.VISIBLE
//                            main_page.isEnabled = false

                            lyt_tabour_logo.isEnabled = true
                            lyt_my_bookings.isEnabled = true
                            lyt_contact_us.isEnabled = true
                            lyt_tabour_policy.isEnabled = true
                            lyt_termsncndtns.isEnabled = true
                            lyt_logout.isEnabled = true
                            lyt_logout_deactivate.isEnabled = true

                            val params = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                            )
                            params.setMargins(40, 10, 40, 20)
                            lyt_main.setLayoutParams(params)

                        }
                    }
                ).start()
        } else {
            view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .translationX(view.width.toFloat() / -2.0f)
                .setDuration(300)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animator: Animator) {
                            super.onAnimationStart(animator)

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)

                            secondary_back.visibility = View.VISIBLE

                            val params = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                            )
                            params.setMargins(40, 10, 40, 20)
                            lyt_main.setLayoutParams(params)
                        }
                    }
                ).start()
        }



        return null

    }

    fun hideMenu(view: View, curr_language: String): ObjectAnimator? {
        isHide = true

        if (curr_language.equals("en", ignoreCase = true)) {
            view.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationX(0.0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator) {
                        super.onAnimationEnd(animator)

                    }

                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)

                        secondary_back.visibility = View.GONE
//                        main_page.isEnabled = true

                        lyt_tabour_logo.isEnabled = false
                        lyt_my_bookings.isEnabled = false
                        lyt_contact_us.isEnabled = false
                        lyt_tabour_policy.isEnabled = false
                        lyt_termsncndtns.isEnabled = false
                        lyt_logout.isEnabled = false
                        lyt_logout_deactivate.isEnabled = false

                        val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        params.setMargins(0, 0, 0, 0)
                        lyt_main.setLayoutParams(params)

                    }

                }).start()
        } else {
            view.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationX(0.0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator) {
                        super.onAnimationEnd(animator)
//                    this@MainActivity.content_view.setRadius(
//                        Tools.dpToPx(this@MainActivity.getApplicationContext(), 0)
//                            .toFloat()
//                    )

//                        secondary_back.visibility = View.GONE
//                        val params = RelativeLayout.LayoutParams(
//                            RelativeLayout.LayoutParams.MATCH_PARENT,
//                            RelativeLayout.LayoutParams.MATCH_PARENT
//                        )
//                        params.setMargins(0, 0, 0, 0)
//                        lyt_main.setLayoutParams(params)

                    }

                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)

                        secondary_back.visibility = View.GONE
                        val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        params.setMargins(0, 0, 0, 0)
                        lyt_main.setLayoutParams(params)

                    }

                }).start()
        }


        return null

    }

    fun checkStrings() {
        val person = "Android"
        Log.d("chechStr", person)
        val outputStr = person.replace("Android", "Kotlin", ignoreCase = false)
        Log.d("chechStr", outputStr)
    }

    // Modern approch
    fun showEnableLocationSetting() {
        this.let {
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
                        e.startResolutionForResult(
                            it,
                            this.REQUEST_CHECK_SETTINGS
                        )

//                        startIntentSenderForResult(e.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)

                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.i("check", "User agreed to make required location settings changes.")
                        getLocation()
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.i(
                            "check",
                            "User choose not to make required location settings changes."
                        )
                    }
                }
            }
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        list = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>

                        Log.d(
                            "currentLocation",
                            "Latitude\n${list[0].latitude}\nLongitude\\n${list[0].longitude}"
                        )

                        HomeViewModel.location_name.value = list[0].latitude.let {
                            CurrentLocation(
                                list[0].latitude,
                                list[0].longitude,
                                AppCommonUtils.getPlaceFromLatLng(
                                    this,
                                    "",
                                    list[0].latitude,
                                    list[0].longitude
                                )
                            )
                        }

                        SharedViewModel.currlocation_name.value = list[0].latitude.let {
                            CurrentLocation(
                                list[0].latitude,
                                list[0].longitude,
                                AppCommonUtils.getPlaceFromLatLng(
                                    this,
                                    "",
                                    list[0].latitude,
                                    list[0].longitude
                                )
                            )
                        }



//                        mainBinding.apply {
//                            tvLatitude.text = "Latitude\n${list[0].latitude}"
//                            tvLongitude.text = "Longitude\n${list[0].longitude}"
//                            tvCountryName.text = "Country Name\n${list[0].countryName}"
//                            tvLocality.text = "Locality\n${list[0].locality}"
//                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
//                        }
                    }
                }

            } else {
                showEnableLocationSetting()
            }
        } else {
            requestPermissions()
        }
    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.lyt_exprt_logo -> {
                AppCommonUtils.hideKeyboard(this)
                hideMenu(content_view, curr_language)
                AppCommonUtils.loadFragment(this@MainActivity, Profile())
            }
            R.id.lyt_my_bookings -> {
                AppCommonUtils.hideKeyboard(this)

                lyt_my_bookings.background = ContextCompat.getDrawable(this,R.drawable.selected_nav_bg)
                lyt_contact_us.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_tabour_policy.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_termsncndtns.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                tv_my_bookings.setTextColor(ContextCompat.getColor(this,R.color.sidenav_selected_txtcolor))
                tv_contact_us.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_tabour_policy.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_termsncndtns.setTextColor(ContextCompat.getColor(this,R.color.white))

                hideMenu(content_view, curr_language)

                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(this@MainActivity)
                }
                else
                {
                    AppCommonUtils.loadFragment(this@MainActivity, BookingHistory())
                }
            }
            R.id.lyt_contact_us -> {
                AppCommonUtils.hideKeyboard(this)

                lyt_my_bookings.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_contact_us.background = ContextCompat.getDrawable(this,R.drawable.selected_nav_bg)
                lyt_tabour_policy.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_termsncndtns.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                tv_my_bookings.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_contact_us.setTextColor(ContextCompat.getColor(this,R.color.sidenav_selected_txtcolor))
                tv_tabour_policy.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_termsncndtns.setTextColor(ContextCompat.getColor(this,R.color.white))

                hideMenu(content_view, curr_language)
                val bundle = Bundle()
                bundle.putString("pagename", "Contact Us")
                bundle.putString("pageurl","https://tabour.qa/en/app_contact_us")
                val termsandpolicies = Termsandpolicies()
                termsandpolicies.arguments = bundle
                AppCommonUtils.loadFragment(this@MainActivity, termsandpolicies)
            }
            R.id.lyt_tabour_policy -> {
                AppCommonUtils.hideKeyboard(this)

                lyt_my_bookings.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_contact_us.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_tabour_policy.background = ContextCompat.getDrawable(this,R.drawable.selected_nav_bg)
                lyt_termsncndtns.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                tv_my_bookings.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_contact_us.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_tabour_policy.setTextColor(ContextCompat.getColor(this,R.color.sidenav_selected_txtcolor))
                tv_termsncndtns.setTextColor(ContextCompat.getColor(this,R.color.white))

                hideMenu(content_view, curr_language)
                val bundle = Bundle()
                bundle.putString("pagename", "Privacy Policy")
                bundle.putString("pageurl","https://tabour.qa/en/app_privacy_policy")
                val termsandpolicies = Termsandpolicies()
                termsandpolicies.arguments = bundle
                AppCommonUtils.loadFragment(this@MainActivity, termsandpolicies)
            }
            R.id.lyt_termsncndtns -> {
                AppCommonUtils.hideKeyboard(this)

                lyt_my_bookings.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_contact_us.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_tabour_policy.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                lyt_termsncndtns.background = ContextCompat.getDrawable(this,R.drawable.selected_nav_bg)
                tv_my_bookings.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_contact_us.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_tabour_policy.setTextColor(ContextCompat.getColor(this,R.color.white))
                tv_termsncndtns.setTextColor(ContextCompat.getColor(this,R.color.sidenav_selected_txtcolor))

                hideMenu(content_view, curr_language)
                val bundle = Bundle()
                bundle.putString("pagename", "Terms and conditions")
                bundle.putString("pageurl","https://tabour.qa/en/app_terms_conditions")
                val termsandpolicies = Termsandpolicies()
                termsandpolicies.arguments = bundle
                AppCommonUtils.loadFragment(this@MainActivity, termsandpolicies)
            }
            R.id.lyt_logout -> {
                homeViewModel.logout(this)
            }
            R.id.lyt_login -> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            R.id.lyt_logout_deactivate -> {
                AppCommonUtils.showDeactivateAccDialog(this@MainActivity, homeViewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(NetworkConnectionReceiver(), filter)

//        Log.e("OneSignalExample", "onStart is called..")
//        mode = intent.getStringExtra("mode").toString()
//        Log.e("OneSignalExample", "mode is ${mode}")
//
//        if(mode.equals("notification"))
//        {
//            Log.e("OneSignalExample", "Notification mode is called..")
//            category = intent.getStringExtra("category").toString()
//            booking_id = intent.getStringExtra("booking_id").toString()
//            Log.e("OneSignalExample", "category is: ${category}")
//            when(category)
//            {
//                "5" -> {
//                    Log.e("OneSignalExample", "category value: ${category}")
//                    AppCommonUtils.loadFragment(this, NotificationFragment())
//                }
//                "1" ->{
//                    Log.e("OneSignalExample", "category value: ${category}")
//                    AppCommonUtils.loadFragment(this, NotificationFragment())
//                }
//                "3" ->{
//                    Log.e("OneSignalExample", "category value: 3")
//                    AppCommonUtils.hideKeyboard(this)
//
//                    val bundle = Bundle()
//                    bundle.putString("reservation_id",booking_id)
//                    bundle.putString("category", "1")
//                    val viewBooking = ViewBooking()
//                    viewBooking.arguments = bundle
//                    AppCommonUtils.loadFragment(this@MainActivity, viewBooking)
//                }
//                "4" -> {
//
//                    AppCommonUtils.hideKeyboard(this)
//
//                    val bundle = Bundle()
//                    bundle.putString("mode","user_in_queue")
//                    bundle.putString("category", "1")
//                    bundle.putString("user_queue_id",booking_id)
//                    val viewQueueBooking = ViewQueueBooking()
//                    viewQueueBooking.arguments = bundle
//                    AppCommonUtils.loadFragment(this@MainActivity, viewQueueBooking)
//
//                }
//            }
//        }
//        else{
//            Log.e("OneSignalExample", "HomeFragment fragment is called..")
//            AppCommonUtils.loadFragment(this@MainActivity, HomeFragment())
//        }

    }

    override fun onResume() {
        super.onResume()
        Log.e("OneSignalExample", "onResume is called..")
    }

    override fun onPause() {
        super.onPause()

        Log.e("OneSignalExample", "onPause is called..")

    }

    override fun onStop() {
        super.onStop()

        try{
            unregisterReceiver(NetworkConnectionReceiver())
        }catch (ex: java.lang.Exception)
        {
            Log.d("checkexception","${ex.message} raised")
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("OneSignalExample", "onDestroy is called..")

    }


}
package com.tabour.hospitality.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.activity.RegisterActivity
import com.tabour.hospitality.base.MyApplicationClass
import com.tabour.hospitality.models.*
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppsDataUrls
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.LoginViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class HomeRepo {

    lateinit var categoriesList: ArrayList<Categories>
    lateinit var explrRestaurantList: ArrayList<ExploreRes>
    lateinit var nearbyResList: ArrayList<NearbyRes>
    lateinit var availnowResList: ArrayList<AvailableNow>
    lateinit var upcomingEventsList: ArrayList<UpComingEvents>

    companion object {
        var mInstance: HomeRepo? = null

        fun getInstance(): HomeRepo {

            if (mInstance == null) {
                synchronized(this)
                {
                    mInstance = HomeRepo()
                }
            }

            return mInstance!!
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getCategories(context: Context) {
        HomeViewModel.cat_progressbar.postValue(true)

        val params = JSONObject()

        if (AppPreferenceStorage.getUserid().equals("")) {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        } else {
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        Log.d("checkCategories", params.toString())

        val getrescategories = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getAllCatUrl(),
            params,
            {
                categoriesList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    HomeViewModel.cat_progressbar.postValue(false)

                    val status = it.getString("status")

                    when (status) {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {
                                val dataObj = dataArr.getJSONObject(i)
                                val categories = Categories(
                                    dataObj.getInt("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("image")
                                )

                                categoriesList.add(categories)
                            }

                            if (categoriesList.size > 0) {
                                HomeViewModel.res_categories.postValue(categoriesList)
                            } else {
                                HomeViewModel.res_categories.postValue(categoriesList)
                            }
                        }
                        "fail" -> {
                            HomeViewModel.res_categories.postValue(categoriesList)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "blocked" -> {
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            })
        {
            HomeViewModel.cat_progressbar.postValue(false)
        }

        getrescategories.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getrescategories)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getExploreRestaurants(context: Context) {
        HomeViewModel.explr_progressbar.postValue(true)

        val params = JSONObject()

        if (AppPreferenceStorage.getUserid().equals("")) {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        } else {
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        Log.i("checkparams", "Explore ${params.toString()}")

        val getexploreRestaurants = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getExploreResUrl(),
            params,
            {
                explrRestaurantList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    HomeViewModel.explr_progressbar.postValue(false)

                    val status = it.getString("status")

                    when (status) {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            val singleCuisineType = { cuisine_type_name: String ->

                                var cuisine_name = ""

                                val cuisineList = cuisine_type_name.split(",")
                                cuisine_name = cuisineList[0]

                                cuisine_name
                            }

                            for (i in 0 until dataArr.length()) {
                                val dataObj = dataArr.getJSONObject(i)
                                val exploreRes = ExploreRes(
                                    dataObj.getString("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("address"),
                                    singleCuisineType(dataObj.getString("cuisine_type")),
                                    dataObj.getString("book_now_btn"),
                                    dataObj.getString("queue_btn"),
                                    dataObj.getString("is_saved"),
                                    dataObj.getString("image")
                                )

                                explrRestaurantList.add(exploreRes)
                            }

                            if (explrRestaurantList.size > 0) {
                                HomeViewModel.explrRestaurantListData.postValue(explrRestaurantList)
                            } else {
                                HomeViewModel.explrRestaurantListData.postValue(explrRestaurantList)
                            }
                        }
                        "fail" -> {
                            HomeViewModel.explrRestaurantListData.postValue(explrRestaurantList)
                        }
                        "blocked" -> {
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            })
        {
            HomeViewModel.explr_progressbar.postValue(false)
        }

        getexploreRestaurants.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getexploreRestaurants)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getavailablenowRestaurants(
        context: Context,
        lattitude: Double,
        longitude: Double,
        restro_type: String,
        date: String,
        time: String,
        cuisine_type: String
    ) {
        HomeViewModel.availnow_progressbar.postValue(true)

        val params = JSONObject()
        if (AppPreferenceStorage.getUserid().equals("")) {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        } else {
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        params.put("restro_type", restro_type)
        params.put("selected_date", date)
        params.put("time_slot", time)
        params.put("cuisine_type", cuisine_type)
        params.put("latitude", lattitude.toString())
        params.put("longitude", longitude.toString())

        Log.i("checkparams", "Available ${params.toString()}")

        val getavailablenowrestaurants = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getAvailableNowResUrl(),
            params,
            {
                availnowResList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    HomeViewModel.availnow_progressbar.postValue(false)

                    val status = it.getString("status")

                    when (status) {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val singleCuisineType =
                                    { cuisines: String, cuisine_type: String, restro_cuisines: JSONArray ->

                                        var cuisine_name = ""
                                        if (cuisine_type.equals("")) {
                                            val cuisineList = cuisines.split(",")
                                            cuisine_name = cuisineList[0]
                                        } else {
                                            for (j in 0 until restro_cuisines.length()) {
                                                val cusineObj = restro_cuisines.getJSONObject(j)
                                                if (cusineObj.getString("cuisine_id")
                                                        .equals(cuisine_type)
                                                ) {
                                                    cuisine_name =
                                                        cusineObj.getString("cuisine_name")
                                                }
                                            }
                                        }

                                        cuisine_name
                                    }

                                val availableSlots = { slotsArr: JSONArray, currTime: String ->
                                    val slotsList = ArrayList<String>()

                                    for (j in 0 until slotsArr.length()) {
                                        val slotObj = slotsArr.getJSONObject(j)

                                        if (DateToStringConversion.compareTwoTimes(
                                                currTime,
                                                slotObj.getString("slot")
                                            )
                                        ) {
                                            slotsList.add(slotObj.getString("slot"))
                                        }
//                                    if(slotObj.getString("slot").equals(currTime))
                                    }

                                    slotsList

                                }

                                val dataObj = dataArr.getJSONObject(i)
                                val availableNow = AvailableNow(
                                    dataObj.getInt("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("address"),
                                    singleCuisineType(
                                        dataObj.getString("cuisine_type"),
                                        cuisine_type,
                                        dataObj.getJSONArray("restro_cuisines")
                                    ),
                                    dataObj.getString("book_now_btn"),
                                    dataObj.getString("queue_btn"),
                                    dataObj.getString("is_saved"),
                                    dataObj.getString("image"),
                                    availableSlots(
                                        dataObj.getJSONArray("available_slots"),
                                        DateToStringConversion.getCurrTime()
                                    )
                                )

                                availnowResList.add(availableNow)
                            }

                            if (availnowResList.size > 0) {
                                HomeViewModel.availnowResListData.postValue(availnowResList)
                            } else {
                                HomeViewModel.availnowResListData.postValue(availnowResList)
                            }
                        }
                        "fail" -> {
                            HomeViewModel.availnowResListData.postValue(availnowResList)
                        }
                        "blocked" -> {
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            })
        {
            HomeViewModel.availnow_progressbar.postValue(false)
        }

        getavailablenowrestaurants.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getavailablenowrestaurants)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getNearbyRestaurants(
        context: Context,
        lattitude: Double,
        longitude: Double,
        restro_type: String,
        date: String,
        time: String,
        cuisine_type: String
    ) {
        HomeViewModel.nrby_progressbar.postValue(true)

        val params = JSONObject()
        if (AppPreferenceStorage.getUserid().equals("")) {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        } else {
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        params.put("latitude", lattitude.toString())
        params.put("longitude", longitude.toString())
        params.put("restro_type", restro_type)
        params.put("selected_date", date)
        params.put("time_slot", time)
        params.put("cuisine_type", cuisine_type)

        Log.i("checkparams", "NearBy ${params.toString()}")

        val getnearbyRestaurants = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getNearbyResUrl(),
            params,
            {
                nearbyResList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    HomeViewModel.nrby_progressbar.postValue(false)

                    val status = it.getString("status")

                    when (status) {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val singleCuisineType =
                                    { cuisines: String, cuisine_type: String, restro_cuisines: JSONArray ->

                                        var cuisine_name = ""
                                        if (cuisine_type.equals("")) {
                                            val cuisineList = cuisines.split(",")
                                            cuisine_name = cuisineList[0]
                                        } else {
                                            for (j in 0 until restro_cuisines.length()) {
                                                val cusineObj = restro_cuisines.getJSONObject(j)
                                                if (cusineObj.getString("cuisine_id")
                                                        .equals(cuisine_type)
                                                ) {
                                                    cuisine_name =
                                                        cusineObj.getString("cuisine_name")
                                                }
                                            }
                                        }

                                        cuisine_name
                                    }

                                val availableSlots = { slotsArr: JSONArray, currTime: String ->
                                    val slotsList = ArrayList<String>()

                                    for (j in 0 until slotsArr.length()) {
                                        val slotObj = slotsArr.getJSONObject(j)

                                        if (DateToStringConversion.compareTwoTimes(
                                                currTime,
                                                slotObj.getString("slot")
                                            )
                                        ) {
                                            slotsList.add(slotObj.getString("slot"))
                                        }
//                                    if(slotObj.getString("slot").equals(currTime))
                                    }

                                    slotsList

                                }

                                val dataObj = dataArr.getJSONObject(i)
                                val nearbyRes = NearbyRes(
                                    dataObj.getInt("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("address"),
                                    singleCuisineType(
                                        dataObj.getString("cuisine_type"),
                                        cuisine_type,
                                        dataObj.getJSONArray("restro_cuisines")
                                    ),
                                    dataObj.getString("book_now_btn"),
                                    dataObj.getString("queue_btn"),
                                    dataObj.getString("is_saved"),
                                    dataObj.getString("image"),
                                    availableSlots(
                                        dataObj.getJSONArray("available_slots"),
                                        DateToStringConversion.getCurrTime()
                                    )
                                )

                                nearbyResList.add(nearbyRes)
                            }

                            if (nearbyResList.size > 0) {
                                HomeViewModel.nearbyRestaurantListData.postValue(nearbyResList)
                            } else {
                                HomeViewModel.nearbyRestaurantListData.postValue(nearbyResList)
                            }
                        }
                        "fail" -> {
                            HomeViewModel.nearbyRestaurantListData.postValue(nearbyResList)
//                        withContext(Dispatchers.Main)
//                        {
//                            Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
//                                .show()
//                        }
                        }
                        "blocked" -> {
                            AppCommonUtils.clearSession()
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                    if (it.getString("status").equals("success", ignoreCase = true)) {

                    } else {

                    }
                }
            })
        {
            HomeViewModel.nrby_progressbar.postValue(false)
        }

        getnearbyRestaurants.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getnearbyRestaurants)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getupcomingEvents(context: Context) {
        HomeViewModel.upcmgevnts_progressbar.postValue(true)

        val params = JSONObject()
        if (AppPreferenceStorage.getUserid().equals("")) {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        } else {
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        Log.i("checkparams", "Upcoming Events ${params.toString()}")

        val getupcomingevents = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getUpcmgevntsUrl(),
            params,
            {
                upcomingEventsList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    HomeViewModel.upcmgevnts_progressbar.postValue(false)

                    val status = it.getString("status")

                    when (status) {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val res_dttime = { booking_date: String, time_slot: String ->

                                    if (time_slot.equals("")) {
                                        "${DateToStringConversion.converteddate(booking_date)}}"
                                    } else {
                                        "${DateToStringConversion.converteddate(booking_date)}, ${
                                            DateToStringConversion.getTimeinAmPM(
                                                time_slot
                                            )
                                        }"
                                    }
                                }

                                val dataObj = dataArr.getJSONObject(i)
                                val upComingEvents = UpComingEvents(
                                    dataObj.getString("id"),
                                    dataObj.getString("mode"),
                                    dataObj.getString("title"),
                                    dataObj.getString("restro_name"),
                                    dataObj.getString("restro_address"),
                                    dataObj.getString("no_guest"),
                                    dataObj.getString("current_position"),
                                    dataObj.getString("current_waiting_time"),
                                    res_dttime(
                                        dataObj.getString("booking_date"),
                                        dataObj.getString("time_slot")
                                    ),
                                    dataObj.getString("qr_url"),
                                    AppCommonUtils.getQrCodeBitmap(dataObj.getString("qr_url"))
                                )

                                upcomingEventsList.add(upComingEvents)
                            }

                            if (upcomingEventsList.size > 0) {
                                HomeViewModel.upcomingEventsListData.postValue(upcomingEventsList)
                            } else {
                                HomeViewModel.upcomingEventsListData.postValue(upcomingEventsList)
                            }
                        }
                        "fail" -> {
                            HomeViewModel.upcomingEventsListData.postValue(upcomingEventsList)
//                        withContext(Dispatchers.Main)
//                        {
//                            Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
//                                .show()
//                        }
                        }
                        "blocked" -> {
                            AppCommonUtils.clearSession()
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            })
        {
            HomeViewModel.upcmgevnts_progressbar.postValue(false)
        }

        getupcomingevents.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getupcomingevents)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getReadnotificationstatus(context: Context) {
        HomeViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        Log.d("checkReadNotifyStatus", params.toString())

        val getreadstatus = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getReadNotificationStatusUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    HomeViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")
                            HomeViewModel.notification_read_status.postValue(dataObj.getString("status"))
                            HomeViewModel.notification_read_count.postValue(dataObj.getString("unread_notifications"))
                        }
                        "fail" -> {
                            HomeViewModel.notification_read_status.postValue("0")
                            HomeViewModel.notification_read_count.postValue("0")
                        }
                        "blocked" -> {
                            AppCommonUtils.clearSession()
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            }
        ) {
            HomeViewModel.progressbar.postValue(false)
        }

        getreadstatus.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getreadstatus)

    }


    @OptIn(DelicateCoroutinesApi::class)
    fun logout(context: Context) {
        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        Log.d("checklogout", params.toString())
        val logout = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getLogoutUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            AppPreferenceStorage.saveUserid("")
                            AppPreferenceStorage.saveAuthToken("")
                            AppPreferenceStorage.saveCount("")
                            AppPreferenceStorage.saveUsername("")
                            AppPreferenceStorage.saveMobileNum("")
                            AppPreferenceStorage.saveProfilePic("")
                            HomeViewModel.loginStatus.postValue(false)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            HomeViewModel.loginStatus.postValue(true)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "blocked" -> {
                            AppCommonUtils.clearSession()
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            }
        ) {
            HomeViewModel.loginStatus.postValue(true)
        }

        logout.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(logout)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deactivateAccount(context: Context) {
        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        val deactivateaccount = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getDeactivateAccountUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            AppPreferenceStorage.saveUserid("")
                            AppPreferenceStorage.saveAuthToken("")
                            AppPreferenceStorage.saveCount("")
                            AppPreferenceStorage.saveUsername("")
                            AppPreferenceStorage.saveMobileNum("")
                            AppPreferenceStorage.saveProfilePic("")
                            HomeViewModel.activateStatus.postValue("0")
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            HomeViewModel.activateStatus.postValue("1")
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "blocked" -> {
                            AppCommonUtils.clearSession()
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                            (context as MainActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            }
        ) {
            HomeViewModel.activateStatus.postValue("1")
        }

        deactivateaccount.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(deactivateaccount)
    }

}
package com.tabour.hospitality.repository

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.base.MyApplicationClass
import com.tabour.hospitality.fragments.SavedRestaurants
import com.tabour.hospitality.models.*
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppsDataUrls
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.HomeViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class EshtblshmntDtlsRepo {

    lateinit var imageList: ArrayList<String>
    lateinit var timeListByDate: ArrayList<TimeSlot>
    lateinit var seatingoptList: ArrayList<Seating>
    lateinit var histryBookngList: ArrayList<HistryBookng>
    lateinit var menuitemsList: ArrayList<ParentItem>
    lateinit var savedResList: ArrayList<SavedRestaurant>
    lateinit var notificationsList: ArrayList<Notification_model>
    var progressbar = MutableLiveData<Boolean>()

    companion object {
        var mInstance: EshtblshmntDtlsRepo? = null

        fun getInstance(): EshtblshmntDtlsRepo {

            if (mInstance == null) {
                synchronized(this) {
                    mInstance = EshtblshmntDtlsRepo()
                }
            }

            return mInstance!!

        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getEshtblshmntDtls(context: Context, restaurant_id: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        if (AppPreferenceStorage.getUserid().equals("")) {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        } else {
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        params.put("restaurant_id", restaurant_id)

        Log.d("checkresdtlsparams", "Restaurant details: ${params.toString()}")

        val geteshtblshmntDtls =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.restaurantsDtlsUrl(), params, {
                imageList = ArrayList()
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")
                            val imagesArr = dataObj.getJSONArray("images")
                            val wrkingHrsArr = dataObj.getJSONArray("working_hours")

                            val openNow = {
                                var status = ""
                                for (i in 0 until wrkingHrsArr.length()) {
                                    val wrknHrsObj = wrkingHrsArr.getJSONObject(i)
                                    if (DateToStringConversion.getCurrDay()
                                            .equals(wrknHrsObj.getString("day"))
                                    ) {
                                        if(wrknHrsObj.getString("closing_time").equals(""))
                                        {
                                            status = "Closed"
                                        }
                                        else
                                        {
                                            status = DateToStringConversion.checkOpenNow(
                                                DateToStringConversion.getCurrTime(),
                                                wrknHrsObj.getString("opening_time"),
                                                wrknHrsObj.getString("closing_time")
                                            )
                                        }
                                    }
                                }

                                status
                            }

                            val closedTime = {
                                var closedAt = ""
                                val currDay = DateToStringConversion.getCurrDay()
                                for (k in 0 until wrkingHrsArr.length()) {
                                    val wrknHrsObj = wrkingHrsArr.getJSONObject(k)
                                    if (currDay.equals(wrknHrsObj.getString("day"))) {

                                        if(wrknHrsObj.getString("closing_time").equals(""))
                                        {
                                            closedAt = "  -  "
                                        }
                                        else{
                                            closedAt =
                                                DateToStringConversion.getTimeinAmPM(wrknHrsObj.getString("closing_time"))
                                        }
                                    }
                                }
                                closedAt
                            }

                            for (j in 0 until imagesArr.length()) {
                                val imageObj = imagesArr.getJSONObject(j)

                                imageList.add(imageObj.getString("image"))

                            }

                            val getCuisins = { restro_cuisines: JSONArray, cuisine_type: String ->

                                var cuisine_names = ""
                                if(restro_cuisines.length() > 0)
                                {
                                    for(i in 0 until restro_cuisines.length())
                                    {
                                        val cuisineObj = restro_cuisines.getJSONObject(i)
                                        if(cuisine_names.equals(""))
                                        {
                                            cuisine_names = cuisineObj.getString("cuisine_name")
                                        }
                                        else{
                                            cuisine_names = "${cuisine_names}, ${cuisineObj.getString("cuisine_name")}"
                                        }
                                    }
                                }
                                else
                                {
                                    cuisine_names = cuisine_type
                                }
                                cuisine_names
                            }

                            val getSeating = { seatingArr: JSONArray ->

                                var seatingOptions = ""
                                for (k in 0 until seatingArr.length()) {
                                    if(seatingOptions.equals(""))
                                    {
                                        seatingOptions = "${seatingArr.get(k)}"
                                    }
                                    else
                                    {
                                        seatingOptions = "${seatingOptions}, ${seatingArr.get(k)}"
                                    }
                                }
                                seatingOptions
                            }

                            val checkCloseed = { time: String ->

                                var specified_time = ""
                                if(time.equals(""))
                                {
                                    specified_time = "Closed"
                                }
                                else
                                {
                                    specified_time = DateToStringConversion.getTimeinAmPM(time)
                                }

                                specified_time

                            }

                            val working_hours = { working_hours: JSONArray ->

                                val wrknghrsArr = ArrayList<WorkingHours>()
                                for (l in 0 until working_hours.length())
                                {
                                    val wrkhrsObj = working_hours.getJSONObject(l)
                                    val workingHours = WorkingHours(
                                        wrkhrsObj.getString("day"),
                                        checkCloseed(wrkhrsObj.getString("opening_time")),
                                        checkCloseed(wrkhrsObj.getString("closing_time"))
                                    )

                                    wrknghrsArr.add(workingHours)

                                }

                                wrknghrsArr
                            }


                            val eshtblshmntDetails = EshtablishmentDetails(
                                dataObj.getString("id"),
                                dataObj.getString("name"),
                                dataObj.getString("address"),
                                dataObj.getString("latitude"),
                                dataObj.getString("longitude"),
                                dataObj.getString("description"),
                                getCuisins(dataObj.getJSONArray("restro_cuisines"), dataObj.getString("cuisine_type")),
                                dataObj.getString("book_now_btn"),
                                dataObj.getString("queue_btn"),
                                dataObj.getString("is_saved"),
                                getSeating(dataObj.getJSONArray("seating_options")),
                                imageList,
                                openNow(),
                                closedTime(),
                                dataObj.getString("parking"),
                                working_hours(dataObj.getJSONArray("working_hours"))
                            )

                            EshtblshmntViewModel.estbhlshmntDtlsData.postValue(eshtblshmntDetails)
                        }
                        "fail" -> {
                            val eshtblshmntDetails = EshtablishmentDetails(
                                "", "", "", "", "", "", "","", "", "", "", imageList, "", "", "",ArrayList()
                            )
                            EshtblshmntViewModel.estbhlshmntDtlsData.postValue(eshtblshmntDetails)
//                        withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        geteshtblshmntDtls.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(geteshtblshmntDtls)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getRestroSlotsByDate(context: Context, restro_id: String, selected_date: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restro_id", restro_id)
        params.put("selected_date", selected_date)

        Log.i("checkslotsparams", params.toString())

        val getrstrotmbydt =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.getrstrotmbydtUrl(), params, {
                timeListByDate = ArrayList()

                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")
                            val available_slotsArr = dataObj.getJSONArray("available_slots")

                            for (i in 0 until available_slotsArr.length()) {
                                val available_slotsObj = available_slotsArr.getJSONObject(i)

                                val getdsply_time = { slot: String ->
                                    val dsply_time = slot.split(" ")
                                    dsply_time[0]
                                }

                                val getdsply_format = { slot: String ->
                                    val dsply_time = slot.split(" ")
                                    dsply_time[1]
                                }

                                val timeSlot = TimeSlot(
                                    available_slotsObj.getString("slot"),
                                    getdsply_time(available_slotsObj.getString("slot")),
                                    getdsply_format(available_slotsObj.getString("slot"))
                                )

                                timeListByDate.add(timeSlot)

                            }

                            if (timeListByDate.size > 0) {
                                EshtblshmntViewModel.timeSlotsData.postValue(timeListByDate)
                            } else {
                                EshtblshmntViewModel.timeSlotsData.postValue(timeListByDate)
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.timeSlotsData.postValue(timeListByDate)
                            withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.timeSlotsData.postValue(timeListByDate)
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getrstrotmbydt.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getrstrotmbydt)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getRestroDinningAreas(
        context: Context,
        restro_id: String,
        selected_date: String,
        selected_time_slot: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restro_id", restro_id)
        params.put("selected_date", selected_date)
        params.put("selected_time_slot", selected_time_slot)

        Log.i("checkdinningareasparams", params.toString())

        val getrestrodinareas =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.getgetrestrodinareasUrl(), params, {
                seatingoptList = ArrayList()

                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")
                            val seating_optionsArr = dataObj.getJSONArray("seating_options")

                            for (i in 0 until seating_optionsArr.length()) {
                                val seatingObj = seating_optionsArr.getJSONObject(i)
                                val seating = Seating(
                                    seating_option = seatingObj.getString("seating_option"),
                                    id = seatingObj.getString("id")
                                )

                                seatingoptList.add(seating)

                            }

                            if (seatingoptList.size > 0) {
                                EshtblshmntViewModel.seatingoptListData.postValue(seatingoptList)
                            } else {
                                EshtblshmntViewModel.seatingoptListData.postValue(seatingoptList)
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.seatingoptListData.postValue(seatingoptList)
                            withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getrestrodinareas.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getrestrodinareas)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun submitBooking(
        context: Context,
        booking_date: String,
        time_slot: String,
        restaurant_id: String,
        no_guest: String,
        occasion: String,
        dining_area: String,
        special_request: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("booking_date", booking_date)
        params.put("time_slot", time_slot)
        params.put("restaurant_id", restaurant_id)
        params.put("no_guest", no_guest)
        params.put("occasion", occasion)
        params.put("dining_area", dining_area)
        params.put("special_request", special_request)
        params.put("promocode", "")

        Log.i("checkbookingparams", params.toString())

        val submitbooking =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.submitBookingUrl(), params, {
                seatingoptList = ArrayList()

                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)
                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            Log.i("checkbookingparams", "response fetched")
                            val dataObj = it.getJSONObject("data")
//                        EshtblshmntViewModel.booking_status.postValue(true)
                            EshtblshmntViewModel.reservation_id.postValue(dataObj.getString("reservation_id"))
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            //EshtblshmntViewModel.booking_status.postValue(false)
                            EshtblshmntViewModel.reservation_id.postValue("")
                            withContext(Dispatchers.Main) {
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
            }) {
                Log.i("checkbookingparams", "Volley")
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        submitbooking.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(submitbooking)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getChooseSeatnqueue(context: Context, restro_id: String, party_number: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restro_id", restro_id)
        params.put("party_number", party_number)

        Log.i("checkslotsparams", "Seating ${params}")

        val getchooseseatnqueue =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.queueBookingUrl(), params, {
                seatingoptList = ArrayList()

                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val dinningAreaArr = { dining_area: JSONArray ->
                                val dinnList = ArrayList<Seating>()
                                for (i in 0 until dining_area.length()) {
                                    val dinnObj = dining_area.getJSONObject(i)
                                    val seating = Seating(
                                        dinnObj.getString("id"), dinnObj.getString("seating_option")
                                    )
                                    dinnList.add(seating)
                                }
                                dinnList
                            }

                            val chooseSeatingQueueData = ChooseSeatingQueueData(
                                dataObj.getString("restro_id"),
                                dataObj.getString("party_number"),
                                dataObj.getString("postion_in_line"),
                                dataObj.getString("waiting_time"),
                                dinningAreaArr(dataObj.getJSONArray("dining_area"))
                            )

                            EshtblshmntViewModel.chooseSeatingQueue.postValue(chooseSeatingQueueData)
                        }
                        "fail" -> {
                            val chooseSeatingQueueData = ChooseSeatingQueueData(
                                "", "", "", "", ArrayList()
                            )
                            EshtblshmntViewModel.chooseSeatingQueue.postValue(chooseSeatingQueueData)
                            withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getchooseseatnqueue.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getchooseseatnqueue)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkUserInQueue(
        context: Context,
        res_id: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restro_id", res_id)

        Log.i("checkuserinqueue", params.toString())

        val checkuserInQueue =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.checkUserQueueUrl(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val checkQueue = CheckQueue(
                                dataObj.getString("user_is_in_queue"),
                                dataObj.getString("user_queue_id"),
                                res_id
                            )

                            EshtblshmntViewModel.userisinqueueData.postValue(checkQueue)
                        }
                        "fail" -> {
                            val checkQueue = CheckQueue(
                                "",
                                "",
                                ""
                            )
                            EshtblshmntViewModel.userisinqueueData.postValue(checkQueue)
//                        withContext(Dispatchers.Main)
//                        {
//                            Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        checkuserInQueue.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(checkuserInQueue)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getViewBookingqueue(
        context: Context,
        restro_id: String,
        party_number: String,
        dining_area_id: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restaurant_id", restro_id)
        params.put("party_number", party_number)
        params.put("dining_area", dining_area_id)

        Log.i("checkViewBookingqueueparams", params.toString())

        val getviewbookingqueue =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.viewBookingQueueUrl(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val viewQueueBookingData = ViewQueueBookingData(
                                true,
                                "",
                                dataObj.getString("restaurant_id"),
                                dataObj.getString("user_queue_id"),
                                dataObj.getString("initial_position"),
                                dataObj.getString("initial_waiting_time"),
                                dataObj.getString("restaurant_name"),
                                dataObj.getString("restaurant_address"),
                                dataObj.getString("allocated_time_slot"),
                                "${dataObj.getString("booking_date")}, ${dataObj.getString("allocated_time_slot")}",
                                "${dataObj.getString("partynumber")} Guest",
                                dataObj.getString("qr_url"),
                                dataObj.getString("timeout")
                            )

                            EshtblshmntViewModel.viewBooknQueueData.postValue(viewQueueBookingData)
                        }
                        "fail" -> {
                            val viewQueueBookingData = ViewQueueBookingData(
                                false,
                                it.getString("message"),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                            EshtblshmntViewModel.viewBooknQueueData.postValue(viewQueueBookingData)
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getviewbookingqueue.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getviewbookingqueue)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getViewQueueDtls(
        context: Context,
        user_queue_id: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("user_queue_id", user_queue_id)

        Log.d("checkqueuedtlsparams", params.toString())

        val getviewqueueDtls =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.viewQueueDetailsUrl(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val viewQueueBookingData = ViewQueueBookingData(
                                true,
                                "",
                                dataObj.getString("restaurant_id"),
                                dataObj.getString("user_queue_id"),
                                dataObj.getString("current_position"),
                                dataObj.getString("current_waiting_time"),
                                dataObj.getString("restaurant_name"),
                                dataObj.getString("restaurant_address"),
                                dataObj.getString("allocated_time_slot"),
                                "${dataObj.getString("booking_date")}, ${dataObj.getString("allocated_time_slot")}",
                                "${dataObj.getString("partynumber")} Guest",
                                dataObj.getString("qr_url"),
                                dataObj.getString("timeout")
                            )

                            EshtblshmntViewModel.viewBooknQueueData.postValue(viewQueueBookingData)
                        }
                        "fail" -> {
                            val viewQueueBookingData = ViewQueueBookingData(
                                false,
                                it.getString("message"),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                            EshtblshmntViewModel.viewBooknQueueData.postValue(viewQueueBookingData)
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getviewqueueDtls.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getviewqueueDtls)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun ontheWay(context: Context, restaurant_id: String, queue_id: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restaurant_id", restaurant_id)
        params.put("user_queue_id", queue_id)

        Log.i("checkslotsparams", params.toString())

        val ontheway =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.ontheWay(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            EshtblshmntViewModel.onthewayStatus.postValue(true)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.onthewayStatus.postValue(false)
                            withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        ontheway.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(ontheway)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun leaveQueue(context: Context, queue_id: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("user_queue_id", queue_id)

        Log.i("checkslotsparams", params.toString())

        val leavequeue =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.leaveQueueUrl(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            EshtblshmntViewModel.queueLeaveStatus.postValue("1")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.queueLeaveStatus.postValue("0")
                            withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        leavequeue.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(leavequeue)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getViewReservation(context: Context, reservation_id: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("reservation_id", reservation_id)

        Log.i("checkreservationsparams", params.toString())

        val getviewreservation =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.viewReservationUrl(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val res_dttime = { booking_date: String, time_slot: String ->
                                "${DateToStringConversion.converteddate(booking_date)}, ${
                                    DateToStringConversion.getTimeinAmPM(
                                        time_slot
                                    )
                                }"
                            }

                            val viewReservation = ViewReservation(
                                dataObj.getString("id"),
                                dataObj.getString("restro_name"),
                                dataObj.getString("restro_address"),
                                res_dttime(
                                    dataObj.getString("booking_date"),
                                    dataObj.getString("time_slot")
                                ),
                                "${dataObj.getString("no_guest")} Guest",
                                dataObj.getString("occasion"),
                                dataObj.getString("special_request"),
                                dataObj.getString("qr_url"),
                                dataObj.getString("timeout")
                            )

                            EshtblshmntViewModel.viewReservationData.postValue(viewReservation)
                        }
                        "fail" ->
                        {
                            val viewReservation = ViewReservation(
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                            EshtblshmntViewModel.viewReservationData.postValue(viewReservation)
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getviewreservation.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getviewreservation)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun cancelReservation(context: Context, reservation_id: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("reservation_id", reservation_id)

        Log.i("checkreservationsparams", params.toString())

        val cancelreservation =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.cancelReservation(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            EshtblshmntViewModel.bookingcancel_status.postValue(true)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.bookingcancel_status.postValue(true)
                            withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        cancelreservation.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(cancelreservation)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getBookingHistory(
        context: Context,
        mode: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        var dataUrl = ""

        if (mode.equals("upcoming")) {
            dataUrl = AppsDataUrls.uncomingReservationListUrl()
        } else {
            dataUrl = AppsDataUrls.reservationHistoryUrl()
        }

        Log.d("checkSearch", dataUrl)
        Log.d("checkSearch", params.toString())

        val getbookinghistory = JsonObjectRequest(
            Request.Method.POST,
            dataUrl,
            params,
            {
                histryBookngList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val res_dttime = { booking_date: String, time_slot: String ->
                                    "Reserved on ${DateToStringConversion.converteddate(booking_date)}, ${
                                        DateToStringConversion.getTimeinAmPM(
                                            time_slot
                                        )
                                    }"
                                }

                                val dataObj = dataArr.getJSONObject(i)
                                val historyBookng = HistryBookng(
                                    dataObj.getString("id"),
                                    dataObj.getString("reservation_type"),
                                    dataObj.getString("restro_name"),
                                    dataObj.getString("restro_address"),
                                    res_dttime(
                                        dataObj.getString("booking_date"),
                                        dataObj.getString("time_slot")
                                    ),
                                    dataObj.getString("restro_image"),
                                    dataObj.getString("is_saved"),
                                    dataObj.getString("booking_status"),
                                    dataObj.getString("mode")
                                )

                                histryBookngList.add(historyBookng)
                            }

                            if (histryBookngList.size > 0) {
                                EshtblshmntViewModel.histryBookngListData.postValue(histryBookngList)
                            } else {
                                EshtblshmntViewModel.histryBookngListData.postValue(histryBookngList)
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.histryBookngListData.postValue(histryBookngList)
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
            })
        {
            EshtblshmntViewModel.progressbar.postValue(false)
        }

        getbookinghistory.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getbookinghistory)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getNotifications(
        context: Context
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)
        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        Log.d("checkSearch", params.toString())

        val getnotifications = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.getNotificationsUrl(),
            params,
            {
                notificationsList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val dataObj = dataArr.getJSONObject(i)
                                val notificationModel = Notification_model(
                                    dataObj.getString("id"),
                                    dataObj.getString("category"),
                                    dataObj.getString("cat_id"),
                                    dataObj.getString("text"),
                                    dataObj.getString("notification_time")
                                )

                                notificationsList.add(notificationModel)
                            }

                            if (notificationsList.size > 0) {
                                EshtblshmntViewModel.notificationListData.postValue(notificationsList)
                            } else {
                                EshtblshmntViewModel.notificationListData.postValue(notificationsList)
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.notificationListData.postValue(notificationsList)
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
            })
        {
            EshtblshmntViewModel.progressbar.postValue(false)
        }

        getnotifications.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getnotifications)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun saveEshtb(context: Context, restaurant_id: String) {
        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restaurant_id", restaurant_id)

        Log.d("checkSvunsv", "Save ${params.toString()}")

        val saveeshtb = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.saveEshtblshmntUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            EshtblshmntViewModel.saveStatus.postValue("1")
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.saveStatus.postValue("0")
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
            Toast.makeText(context, "Something went wrong...", Toast.LENGTH_LONG).show()
        }

        saveeshtb.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(saveeshtb)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun unSaveEshtb(context: Context, restaurant_id: String) {
        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("restaurant_id", restaurant_id)

        Log.d("checkSvunsv", "UnSave ${params.toString()}")

        val unsaveeshtb = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.unSaveEshtblshmntUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            EshtblshmntViewModel.saveStatus.postValue("0")
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.saveStatus.postValue("1")
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
            Toast.makeText(context, "Something went wrong...", Toast.LENGTH_LONG).show()
        }

        unsaveeshtb.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(unsaveeshtb)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getProfileDetails(context: Context) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        Log.i("checkprofiledetails", params.toString())

        val getprofiledetails =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.getProfileUrl(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val fullname = { f_name: String, l_name: String ->
                                "${f_name} ${l_name}"
                            }
                            val phone_with_countrycode = { country_code: String, phone_num: String ->
                                "${country_code} ${phone_num}"
                            }

                            val profile = Profile(
                                dataObj.getString("id"),
                                dataObj.getString("fname"),
                                dataObj.getString("lname"),
                                fullname(dataObj.getString("fname"), dataObj.getString("lname")),
                                dataObj.getString("country_phonecode"),
                                dataObj.getString("mobile_no"),
                                phone_with_countrycode(
                                    dataObj.getString("country_phonecode"),
                                    dataObj.getString("mobile_no")
                                ),
                                dataObj.getString("email"),
                                dataObj.getString("total_reservations"),
                                dataObj.getString("profile_pic"),
                                ""
                            )

                            EshtblshmntViewModel.profilepicData.postValue(dataObj.getString("profile_pic"))
                            EshtblshmntViewModel.profileData.postValue(profile)
                        }
                        "fail" -> {
                            val profile = Profile(
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                            EshtblshmntViewModel.profileData.postValue(profile)
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getprofiledetails.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getprofiledetails)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateProfileImage(context: Context, profile_pic: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("profile_pic", profile_pic)

        Log.i("checkupdateprofiledetails", params.toString())

        val updateprofileimage =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.updateProfileImage(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            EshtblshmntViewModel.profilepicData.postValue(dataObj.getString("profile_pic"))
                        }
                        "fail" -> {
                            EshtblshmntViewModel.profilepicData.postValue("")
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        updateprofileimage.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(updateprofileimage)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getRestroMenuitems(
        context: Context,
        restro_id: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        if(AppPreferenceStorage.getUserid().equals(""))
        {
            params.put("user_id", "0")
            params.put("auth_token", "0")
        }
        else{
            params.put("user_id", AppPreferenceStorage.getUserid())
            params.put("auth_token", AppPreferenceStorage.getAuthToken())
        }

        params.put("restaurant_id", restro_id)

        Log.i("checkmenuparams", params.toString())

        val getrestromenuitems =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.getRestroMenuitemsUrl(), params, {
                menuitemsList = ArrayList()

                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for(i in 0 until dataArr.length())
                            {
                                val dataObj = dataArr.getJSONObject(i)
                                val menuitemsArr = { items: JSONArray ->

                                    val itemsArr = ArrayList<ChildItem>()
                                    for (j in 0 until items.length())
                                    {
                                        val itemObj = items.getJSONObject(j)
                                        val childItem = ChildItem(
                                            itemObj.getString("item_id"),
                                            itemObj.getString("item_name"),
                                            itemObj.getString("details"),
                                            itemObj.getString("price"),
                                            itemObj.getString("item_image")
                                        )

                                        itemsArr.add(childItem)
                                    }

                                    itemsArr


                                }

                                val parentItem = ParentItem(
                                    dataObj.getString("menu_title"),
                                    menuitemsArr(dataObj.getJSONArray("items"))
                                )

                                menuitemsList.add(parentItem)

                            }

                            if (menuitemsList.size > 0) {
                                EshtblshmntViewModel.resmenuitemsData.postValue(menuitemsList)
                            } else {
                                EshtblshmntViewModel.resmenuitemsData.postValue(menuitemsList)
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.resmenuitemsData.postValue(menuitemsList)
//                        withContext(Dispatchers.Main) {
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        getrestromenuitems.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getrestromenuitems)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateProfileDtls(
        context: Context,
        fname: String,
        lname: String,
        country_phonecode: String,
        mobile_no: String,
        email: String,
        phn_number: String
    ) {
        EshtblshmntViewModel.progressbar.postValue(true)

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())
        params.put("fname", fname)
        params.put("lname", lname)
        params.put("country_phonecode", country_phonecode)
        params.put("mobile_no", mobile_no)
        params.put("email", email)

        Log.i("checkupdateprofiledetails", params.toString())

        val updateprofiledtls =
            JsonObjectRequest(Request.Method.POST, AppsDataUrls.updateProfileDetails(), params, {
                GlobalScope.launch(Dispatchers.IO) {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataObj = it.getJSONObject("data")

                            val fullname = { f_name: String, l_name: String ->
                                "${f_name} ${l_name}"
                            }
                            val phone_with_countrycode = { country_code: String, phone_num: String ->
                                "${country_code} ${phone_num}"
                            }

                            val profile = Profile(
                                dataObj.getString("id"),
                                dataObj.getString("fname"),
                                dataObj.getString("lname"),
                                fullname(dataObj.getString("fname"), dataObj.getString("lname")),
                                dataObj.getString("country_phonecode"),
                                dataObj.getString("mobile_no"),
                                phone_with_countrycode(
                                    dataObj.getString("country_phonecode"),
                                    dataObj.getString("mobile_no")
                                ),
                                dataObj.getString("email"),
                                dataObj.getString("total_reservations"),
                                dataObj.getString("profile_pic"),
                                phn_number
                            )

                            EshtblshmntViewModel.profileData.postValue(profile)
                        }
                        "fail" -> {
                            //                        val profile = Profile(
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                        )
//                        EshtblshmntViewModel.profileData.postValue(profile)
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
            }) {
                EshtblshmntViewModel.progressbar.postValue(false)
            }

        updateprofiledtls.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(updateprofiledtls)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getSavedRestaurants(context: Context, mode: String) {
        EshtblshmntViewModel.progressbar.postValue(true)

        var dataUrl = ""
        if(mode.equals("try_new"))
        {
            dataUrl = AppsDataUrls.getSavedResListUrl()
        }
        else
        {
            dataUrl = AppsDataUrls.getBookagainResUrl()
        }

        val params = JSONObject()
        params.put("user_id", AppPreferenceStorage.getUserid())
        params.put("auth_token", AppPreferenceStorage.getAuthToken())

        Log.i("checkSavedResparams","Saved Restaurant ${params}")

        val getsavedRestaurants = JsonObjectRequest(
            Request.Method.POST,
            dataUrl,
            params,
            {
                savedResList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    EshtblshmntViewModel.progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val singleCuisineType = { cuisines: String, cuisine_type: String, restro_cuisines: JSONArray ->

                                    var cuisine_name = ""
                                    if(cuisine_type.equals(""))
                                    {
                                        val cuisineList = cuisines.split(",")
                                        cuisine_name = cuisineList[0]
                                    }
                                    else
                                    {
                                        for(j in 0 until restro_cuisines.length())
                                        {
                                            val cusineObj = restro_cuisines.getJSONObject(j)
                                            if(cusineObj.getString("cuisine_id").equals(cuisine_type))
                                            {
                                                cuisine_name = cusineObj.getString("cuisine_name")
                                            }
                                        }
                                    }

                                    cuisine_name
                                }

                                val availableSlots = { slotsArr: JSONArray, currTime: String ->
                                    val slotsList = ArrayList<String>()

                                    for(j in 0 until slotsArr.length())
                                    {
                                        val slotObj = slotsArr.getJSONObject(j)

                                        if(DateToStringConversion.compareTwoTimes(currTime,slotObj.getString("slot")))
                                        {
                                            slotsList.add(slotObj.getString("slot"))
                                        }
//                                    if(slotObj.getString("slot").equals(currTime))
                                    }

                                    slotsList

                                }

                                val dataObj = dataArr.getJSONObject(i)
                                val savedRestaurant = SavedRestaurant(
                                    dataObj.getInt("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("address"),
                                    dataObj.getString("cuisine_type"),
                                    dataObj.getString("book_now_btn"),
                                    dataObj.getString("queue_btn"),
                                    dataObj.getString("is_saved"),
                                    dataObj.getString("image"),
                                    availableSlots(dataObj.getJSONArray("available_slots"),DateToStringConversion.getCurrTime())
                                )

                                savedResList.add(savedRestaurant)
                            }

                            if (savedResList.size > 0) {
                                EshtblshmntViewModel.savedResListData.postValue(savedResList)
                            } else {
                                EshtblshmntViewModel.savedResListData.postValue(savedResList)
                            }
                        }
                        "fail" -> {
                            EshtblshmntViewModel.savedResListData.postValue(savedResList)
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
            })
        {
            EshtblshmntViewModel.progressbar.postValue(false)
        }

        getsavedRestaurants.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getsavedRestaurants)

    }

}
package com.tabour.hospitality.viewmodels

import android.app.assist.AssistContent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.base.MyApplicationClass
import com.tabour.hospitality.models.*
import com.tabour.hospitality.repository.EshtblshmntDtlsRepo
import com.tabour.hospitality.repository.HomeRepo
import com.tabour.hospitality.repository.LoginRepo
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppsDataUrls
import com.tabour.hospitality.utils.DateToStringConversion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class EshtblshmntViewModel : ViewModel() {

    lateinit var eshtblshmntDtlsRepo: EshtblshmntDtlsRepo
    lateinit var lRepo: LoginRepo

    companion object {
        var progressbar = MutableLiveData<Boolean>()
        var restroslotsProgressbar = MutableLiveData<Boolean>()
        var estbhlshmntDtlsData = MutableLiveData<EshtablishmentDetails>()
        var timeSlotsData = MutableLiveData<ArrayList<TimeSlot>>()
        var seatingoptListData = MutableLiveData<ArrayList<Seating>>()
        var booking_date = MutableLiveData<String>()
        var time_slot = MutableLiveData<String>()
        var booking_time_seating = MutableLiveData<String>()
        var bookingdate_time = MutableLiveData<String>()
        var restro_id = MutableLiveData<String>()
        var restro_name = MutableLiveData<String>()
        var restro_address = MutableLiveData<String>()
        var no_guest = MutableLiveData<String>()
        var occasion = MutableLiveData<String>()
        var dining_area = MutableLiveData<String>()
        var dining_area_id = MutableLiveData<String>()
        var special_request = MutableLiveData<String>()
        var booking_status = MutableLiveData<Boolean>()
        var bookingcancel_status = MutableLiveData<Boolean>()
        var reservation_id = MutableLiveData<String>()
        var chooseSeatingQueue = MutableLiveData<ChooseSeatingQueueData>()
        var viewReservationData = MutableLiveData<ViewReservation>()
        var userisinqueueData = MutableLiveData<CheckQueue>()
        var viewBooknQueueData = MutableLiveData<ViewQueueBookingData>()
        var queueLeaveStatus = MutableLiveData<String>()
        var onthewayStatus = MutableLiveData<Boolean>()
        var saveStatus = MutableLiveData<String>()
        var histryBookngListData = MutableLiveData<ArrayList<HistryBookng>>()
        var profileData = MutableLiveData<Profile>()
        var profilepicData = MutableLiveData<String>()
        var resmenuitemsData = MutableLiveData<ArrayList<ParentItem>>()
        var savedResListData = MutableLiveData<ArrayList<SavedRestaurant>>()
        var notificationListData = MutableLiveData<ArrayList<Notification_model>>()
    }

    fun getEshtblshmntDtls(content: Context, restaurant_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getEshtblshmntDtls(context = content, restaurant_id)
        }
    }

    fun getRestroSlotsByDate(context: Context, restro_id: String, selected_date: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getRestroSlotsByDate(context, restro_id, selected_date)
        }
    }

    fun getRestroDinningArea(
        context: Context,
        restro_id: String,
        selected_date: String,
        selected_time_slot: String
    ) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getRestroDinningAreas(
                context,
                restro_id,
                selected_date,
                selected_time_slot
            )
        }
    }

    fun submitBooking(
        context: Context,
        booking_date: String,
        time_slot: String,
        restaurant_id: String,
        no_guest: String,
        occasion: String,
        dining_area: String,
        special_request: String
    ) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.submitBooking(
                context,
                booking_date,
                time_slot,
                restaurant_id,
                no_guest,
                occasion,
                dining_area,
                special_request
            )
        }
    }

    fun getChooseSeatnqueue(context: Context, restro_id: String, party_number: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getChooseSeatnqueue(context, restro_id, party_number)
        }
    }

    fun getViewBookingqueue(
        context: Context,
        restro_id: String,
        party_number: String,
        dining_area_id: String
    ) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getViewBookingqueue(
                context,
                restro_id,
                party_number,
                dining_area_id
            )
        }
    }

    fun getViewQueueDtls(
        context: Context,
        user_queue_id: String
    ){
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getViewQueueDtls(
                context,
                user_queue_id
            )
        }
    }


    fun checkUserInQueue(context: Context, res_id: String)
    {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.checkUserInQueue(
                context, res_id
            )
        }
    }


    fun ontheWay(context: Context, restaurant_id: String, queue_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.ontheWay(context, restaurant_id, queue_id)
        }

    }

    fun leaveQueue(context: Context, queue_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.leaveQueue(context, queue_id)
        }

    }

    fun getViewReservation(context: Context, reservation_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getViewReservation(context, reservation_id)
        }
    }

    fun cancelReservation(context: Context, reservation_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.cancelReservation(context, reservation_id)
        }
    }

    fun saveEshtb(context: Context, restaurant_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.saveEshtb(context, restaurant_id)
        }

    }

    fun unSaveEshtb(context: Context, restaurant_id: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.unSaveEshtb(context, restaurant_id)
        }

    }

    fun getBookingHistory(context: Context, mode: String) {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO) {
            eshtblshmntDtlsRepo.getBookingHistory(context, mode)
        }
    }

    fun getProfileDetails(context: Context){
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getProfileDetails(context)
        }
    }

    fun updateProfileImage(context: Context, profile_pic: String){
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.updateProfileImage(context, profile_pic)
        }

    }

    fun updateProfileDtls(
        context: Context,
        fname: String,
        lname: String,
        country_phonecode: String,
        mobile_no: String,
        email: String,
        phn_number: String
    ){
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.updateProfileDtls(
                context,
                fname,
                lname,
                country_phonecode,
                mobile_no,
                email,
                phn_number
            )
        }

    }

    fun getRestroMenuitems(
        context: Context,
        restro_id: String
    )
    {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getRestroMenuitems(context, restro_id)
        }
    }


    fun getCountriesList(context: Context) {
        lRepo = LoginRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            lRepo.getCountries(context)
        }
    }

    fun getSavedRestaurants(context: Context, mode: String)
    {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getSavedRestaurants(context, mode)
        }
    }

    fun getNotifications(context: Context)
    {
        eshtblshmntDtlsRepo = EshtblshmntDtlsRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            eshtblshmntDtlsRepo.getNotifications(context)
        }

    }

}
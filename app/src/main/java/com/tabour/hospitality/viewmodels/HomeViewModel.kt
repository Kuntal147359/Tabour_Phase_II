package com.tabour.hospitality.viewmodels

import android.content.Context
import android.preference.DialogPreference
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tabour.hospitality.models.*
import com.tabour.hospitality.repository.HomeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    lateinit var hRepo: HomeRepo

    companion object {
        var progressbar = MutableLiveData<Boolean>()
        var cat_progressbar = MutableLiveData<Boolean>()
        var upcmgevnts_progressbar = MutableLiveData<Boolean>()
        var explr_progressbar = MutableLiveData<Boolean>()
        var availnow_progressbar = MutableLiveData<Boolean>()
        var nrby_progressbar = MutableLiveData<Boolean>()
        var res_categories = MutableLiveData<ArrayList<Categories>>()
        var explrRestaurantListData = MutableLiveData<ArrayList<ExploreRes>>()
        var nearbyRestaurantListData = MutableLiveData<ArrayList<NearbyRes>>()
        var availnowResListData = MutableLiveData<ArrayList<AvailableNow>>()
        var upcomingEventsListData = MutableLiveData<ArrayList<UpComingEvents>>()
        var loginStatus = MutableLiveData<Boolean>()
        var activateStatus = MutableLiveData<String>()
        var location_name = MutableLiveData<CurrentLocation>()
        var notification_read_status = MutableLiveData<String>()
        var notification_read_count = MutableLiveData<String>()
    }

    fun getCategories(context: Context) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.getCategories(context)
        }
    }

    fun getupcomingEvents(context: Context) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.getupcomingEvents(context)
        }
    }

    fun getExploreRestaurants(
        context: Context,
        lattitude: Double,
        longitude: Double,
        type: String,
        date: String,
        time: String,
        cuisine_type: String,
    ) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.getExploreRestaurants(context)
        }
    }

    fun getavailablenowRestaurants(
        context: Context,
        lattitude: Double,
        longitude: Double,
        type: String,
        date: String,
        time: String,
        cuisine_type: String
    ) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.getavailablenowRestaurants(
                context,
                lattitude,
                longitude,
                type,
                date,
                time,
                cuisine_type
            )
        }
    }

    fun getNearbyRestaurants(
        context: Context,
        lattitude: Double,
        longitude: Double,
        type: String,
        date: String,
        time: String,
        cuisine_type: String
    ) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.getNearbyRestaurants(
                context,
                lattitude,
                longitude,
                type,
                date,
                time,
                cuisine_type
            )
        }
    }

    fun logout(context: Context) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.logout(context)
        }
    }

    fun deactivateAccount(context: Context) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.deactivateAccount(context)
        }
    }

    fun getSearchRestaurants(context: Context) {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.deactivateAccount(context)
        }
    }

    fun getReadnotificationstatus(context: Context)
    {
        hRepo = HomeRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            hRepo.getReadnotificationstatus(context)
        }

    }


}
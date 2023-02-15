package com.tabour.hospitality.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.tabour.hospitality.models.CurrentLocation

class SharedViewModel : ViewModel() {

    companion object {
        var selectedMonthYear = MutableLiveData<String>()
        var selectedDate = MutableLiveData<String>()
        var selectedTime = MutableLiveData<String>()
        var selectedLocation = MutableLiveData<String>()
        var slctLatLng = MutableLiveData<LatLng>()
        var currlocation_name = MutableLiveData<CurrentLocation>()
        var connectivityStatus = MutableLiveData<Boolean>()
    }

}
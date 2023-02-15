package com.tabour.hospitality.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tabour.hospitality.models.Countries
import com.tabour.hospitality.models.RegisterUser
import com.tabour.hospitality.repository.LoginRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    lateinit var lRepo: LoginRepo

    companion object {
        var progressbarObservable = MutableLiveData<Boolean>()
        var countriesData = MutableLiveData<ArrayList<Countries>>()
        var countryCode = MutableLiveData<String>()
        var phoneNumber = MutableLiveData<String>()
        var onlyphnNumber = MutableLiveData<String>()
        var otp = MutableLiveData<String>()
        var userstatus = MutableLiveData<String>()
        var registerUserData = MutableLiveData<RegisterUser>()
        var registerStatus = MutableLiveData<Boolean>()
        var loginStatus = MutableLiveData<Boolean>()
    }

    fun getCountriesList(context: Context) {
        lRepo = LoginRepo.getInstance()
        viewModelScope.launch(Dispatchers.IO)
        {
            lRepo.getCountries(context)
        }
    }

    fun sendOtp(context: Context, phone_num: String)
    {
        lRepo = LoginRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            lRepo.sendOtp(phone_num = phone_num, context = context)
        }

    }

    fun verifyUser(context: Context, mobile_num: String, device_id: String)
    {
        lRepo = LoginRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            lRepo.verifyUser(context, mobile_num, device_id)
        }

//        userstatus = lRepo.userstatusData
    }

    fun checkRegister(
        fname: String,
        lname: String,
        email: String,
        country_phonecode: String,
        mobile: String
    )
    {
        val registerUser = RegisterUser(fname, lname, email, country_phonecode, mobile)
        registerUserData.postValue(registerUser)
    }

    fun registerUser(
        context: Context,
        fname: String,
        lname: String,
        email: String,
        phn_code: String,
        phn_number: String
    ){
        lRepo = LoginRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            lRepo.register(
                context,
                fname = fname,
                lname = lname,
                email = email,
                country_phonecode = phn_code,
                phone_num = phn_number
            )
        }
    }

    fun login(
        context: Context,
        phn_number: String
    ){
        lRepo = LoginRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            lRepo.login(
                context,
                phn_number
            )
        }
    }




}
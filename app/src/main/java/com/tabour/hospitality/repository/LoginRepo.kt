package com.tabour.hospitality.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.activity.OTPVerify
import com.tabour.hospitality.activity.RegisterActivity
import com.tabour.hospitality.base.MyApplicationClass
import com.tabour.hospitality.models.Countries
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppsDataUrls
import com.tabour.hospitality.viewmodels.LoginViewModel
import com.tabour.hospitality.viewmodels.LoginViewModel.Companion.progressbarObservable
import com.tabour.hospitality.viewmodels.LoginViewModel.Companion.registerStatus
import kotlinx.coroutines.*
import org.json.JSONObject

class LoginRepo {

    lateinit var countriesList: ArrayList<Countries>
    var userstatusData:MutableLiveData<String> = MutableLiveData<String>()

    companion object {
        var mInstance: LoginRepo? = null

        fun getInstance(): LoginRepo {
            if(mInstance == null)
            {
                synchronized(this){
                    mInstance = LoginRepo()
                }
            }
            return mInstance!!
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getCountries(context: Context){
        LoginViewModel.progressbarObservable.postValue(true)
        val getcountries = JsonObjectRequest(
            Request.Method.GET,
            AppsDataUrls.getCountriesUrl(),
            null,
            {
                countriesList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    LoginViewModel.progressbarObservable.postValue(false)
                    if(it.getString("status").equals("success", ignoreCase = true))
                    {
                        val dataArr = it.getJSONArray("data")

                        for(i in 0 until dataArr.length())
                        {
                            val dataObj = dataArr.getJSONObject(i)
                            val countries = Countries(
                                dataObj.getInt("id"),
                                dataObj.getString("name"),
                                dataObj.getString("iso2"),
                                dataObj.getString("phonecode")
                            )

                            countriesList.add(countries)
                        }

                        if(countriesList.size > 0)
                        {
                            LoginViewModel.countriesData.postValue(countriesList)
                        }
                        else
                        {
                            LoginViewModel.countriesData.postValue(countriesList)
                        }

                    }
                    else
                    {
                        LoginViewModel.countriesData.postValue(countriesList)
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
            {
                LoginViewModel.progressbarObservable.postValue(false)
            }

        getcountries.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(getcountries)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendOtp(context: Context, phone_num: String)
    {
        LoginViewModel.progressbarObservable.postValue(true)
        val params = JSONObject()
        params.put("mobile",phone_num)

        Log.d("checkOtpparam", params.toString())

        val sendotp = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.sendOTPUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    LoginViewModel.progressbarObservable.postValue(false)
                    val status = it.getString("status")
                    val abc = status.lowercase()
                    if(abc.equals("success", ignoreCase = true))
                    {
                        LoginViewModel.otp.postValue(it.getString("otp"))
                    }
                    else
                    {
                        LoginViewModel.otp.postValue(it.getString("otp"))
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(context, "Invalid mobile number", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
        ){
            LoginViewModel.progressbarObservable.postValue(false)
            LoginViewModel.otp.postValue("")
            Toast.makeText(context, "Something went wrong, please try after sometime", Toast.LENGTH_LONG).show()
        }

        sendotp.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(sendotp)

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun verifyUser(context: Context, phone_num: String, device_id: String)
    {
        LoginViewModel.progressbarObservable.postValue(true)
        val params = JSONObject()
        params.put("mobile",phone_num)
        params.put("device_id",device_id)

        Log.d("checkverify",params.toString())

        val verifyuser = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.verifyUserUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    LoginViewModel.progressbarObservable.postValue(false)
                    val status = it.getString("status")
                    val abc = status.lowercase()
                    if(abc.equals("success", ignoreCase = true))
                    {
                        val dataObj = it.getJSONObject("data")
                        LoginViewModel.userstatus.postValue(dataObj.getString("userstatus"))
                    }
                    else
                    {
                        LoginViewModel.userstatus.postValue("")
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(context, "Invalid mobile number", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        ){
            LoginViewModel.progressbarObservable.postValue(false)
            LoginViewModel.userstatus.postValue("")
            Toast.makeText(context, "Something went wrong, please try after sometime", Toast.LENGTH_LONG).show()
        }

        verifyuser.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(verifyuser)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun register(context: Context,fname: String, lname: String, email: String,country_phonecode: String, phone_num: String)
    {
        LoginViewModel.progressbarObservable.postValue(true)

        val params = JSONObject()
        params.put("fname",fname)
        params.put("lname",lname)
        params.put("email",email)
        params.put("country_phonecode",country_phonecode)
        params.put("mobile",phone_num)
        params.put("device_type","android")
        params.put("device_id",AppCommonUtils.getDeviceId(context))
        params.put("player_id",AppPreferenceStorage.getPlayerid())

        Log.d("registerparam",params.toString())

        val register = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.registerUser(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    LoginViewModel.progressbarObservable.postValue(false)
                    val status = it.getString("status")
                    val abc = status.lowercase()

                    when(abc)
                    {
                        "success" -> {
                            LoginViewModel.registerStatus.postValue(true)

                            val dataObj = it.getJSONObject("data")
                            AppPreferenceStorage.saveUserid(dataObj.getString("userId"))
                            AppPreferenceStorage.saveAuthToken(dataObj.getString("authToken"))
                            AppPreferenceStorage.saveUsername(dataObj.getString("fname"))
                            AppPreferenceStorage.saveMobileNum(dataObj.getString("mobile_no"))
                            AppPreferenceStorage.saveProfilePic(dataObj.getString("profile_pic"))
                            LoginViewModel.loginStatus.postValue(true)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }
                        "fail" -> {
                            LoginViewModel.registerStatus.postValue(false)
                            LoginViewModel.loginStatus.postValue(false)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }
                        "blocked" -> {
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                            (context as RegisterActivity).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            }
        ){
            LoginViewModel.progressbarObservable.postValue(false)
            LoginViewModel.registerStatus.postValue(false)
            LoginViewModel.loginStatus.postValue(false)
            Toast.makeText(context, "Something went wrong, please try after sometime", Toast.LENGTH_LONG).show()
        }

        register.setRetryPolicy(
            DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        register.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(register)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun login(context: Context, mobile_num : String)
    {
        LoginViewModel.progressbarObservable.postValue(true)

        val params = JSONObject()
        params.put("mobile",mobile_num)
        params.put("device_id",AppCommonUtils.getDeviceId(context))
        params.put("device_type","android")
        params.put("player_id",AppPreferenceStorage.getPlayerid())

        Log.d("checkloginparam", params.toString())

        val loginuser = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.loginUserUrl(),
            params,
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    LoginViewModel.progressbarObservable.postValue(false)

                    val status = it.getString("status")
                    val abc = status.lowercase()
                    Log.d("checkStatus",abc)

                    when(abc)
                    {
                        "success" -> {
                            val phone_with_countrycode = { country_code: String, phone_num: String ->
                                "${country_code} ${phone_num}"
                            }

                            val dataObj = it.getJSONObject("data")
                            AppPreferenceStorage.saveUserid(dataObj.getString("userId"))
                            AppPreferenceStorage.saveAuthToken(dataObj.getString("authToken"))
                            AppPreferenceStorage.saveUsername(dataObj.getString("fname"))
                            AppPreferenceStorage.saveMobileNum(phone_with_countrycode(dataObj.getString("country_phonecode"),dataObj.getString("mobile_no")))
                            AppPreferenceStorage.saveProfilePic(dataObj.getString("profile_pic"))
                            LoginViewModel.loginStatus.postValue(true)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }
                        "fail" -> {
                            LoginViewModel.loginStatus.postValue(false)
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }
                        "blocked" -> {
                            withContext(Dispatchers.Main)
                            {
                                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
                            }
                            (context as OTPVerify).finish()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            }
        ){
            LoginViewModel.progressbarObservable.postValue(false)
            LoginViewModel.loginStatus.postValue(false)
            Toast.makeText(context, "Something went wrong, please try after sometime", Toast.LENGTH_LONG).show()
        }

        loginuser.setRetryPolicy(
            DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        loginuser.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(loginuser)

    }

    suspend fun experiments(context: Context) {

        val getTags: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            AppsDataUrls.getCountriesUrl(),
            null,
            Response.Listener<JSONObject> { response ->
                LoginViewModel.progressbarObservable.postValue(false)

                GlobalScope.launch(Dispatchers.IO) {

                }
            },Response.ErrorListener {
                LoginViewModel.progressbarObservable.postValue(false)
            })
        {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                headers["Authorization"] = "Basic <<YOUR BASE64 USER:PASS>>"
                return headers
            }

        }

        getTags.setShouldCache(false)
        MyApplicationClass.getInstance()!!.addToRequestQueue(getTags)

    }

}
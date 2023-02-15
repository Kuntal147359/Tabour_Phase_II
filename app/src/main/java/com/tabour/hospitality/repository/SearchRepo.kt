package com.tabour.hospitality.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.base.MyApplicationClass
import com.tabour.hospitality.models.Eshtablishment
import com.tabour.hospitality.models.NearbyRes
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppsDataUrls
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SearchViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class SearchRepo {

    lateinit var eshtblshmntList: ArrayList<Eshtablishment>

    companion object {
        var mInstance: SearchRepo? = null

        fun getInstance(): SearchRepo {
            if (mInstance == null) {
                synchronized(this)
                {
                    mInstance = SearchRepo()
                }
            }

            return mInstance!!

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getEshtablishment(
        context: Context,
        restro_name: String,
        seating_options: String,
        distance_radius: String
    ) {
        SearchViewModel.eshtb_progressbar.postValue(true)

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

        params.put("latitude", 21.116543)
        params.put("longitude", 79.070976)
        params.put("restro_name", restro_name)
        params.put("seating_options", seating_options)
        params.put("distance_radius", distance_radius)

        Log.d("checkSearch",params.toString())

        val geteshtablishment = JsonObjectRequest(
            Request.Method.POST,
            AppsDataUrls.searchEshtablishmentUrl(),
            params,
            {
                eshtblshmntList = ArrayList()

                GlobalScope.launch(Dispatchers.IO)
                {
                    SearchViewModel.eshtb_progressbar.postValue(false)

                    val status = it.getString("status")

                    when(status)
                    {
                        "success" -> {
                            val dataArr = it.getJSONArray("data")

                            for (i in 0 until dataArr.length()) {

                                val singleCuisineType = { cuisines: String ->

                                    val cuisineList = cuisines.split(",")
                                    cuisineList[0]
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
                                val eshtablishment = Eshtablishment(
                                    dataObj.getInt("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("address"),
                                    dataObj.getString("cuisine_type"),
                                    dataObj.getString("book_now_btn"),
                                    dataObj.getString("queue_btn"),
                                    dataObj.getString("is_saved"),
                                    dataObj.getString("image"),
                                    availableSlots(dataObj.getJSONArray("available_slots"),
                                        DateToStringConversion.getCurrTime())
                                )

                                eshtblshmntList.add(eshtablishment)
                            }

                            if (eshtblshmntList.size > 0) {
                                SearchViewModel.eshtblshmntListData.postValue(eshtblshmntList)
                            } else {
                                SearchViewModel.eshtblshmntListData.postValue(eshtblshmntList)
                            }
                        }
                        "fail" -> {
                            SearchViewModel.eshtblshmntListData.postValue(eshtblshmntList)
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
                    if (it.getString("status").equals("success", ignoreCase = true)) {

                    } else {

                    }
                }
            })
        {
            SearchViewModel.eshtb_progressbar.postValue(false)
        }

        geteshtablishment.setShouldCache(false)
        MyApplicationClass.getInstance().addToRequestQueue(geteshtablishment)

    }

}
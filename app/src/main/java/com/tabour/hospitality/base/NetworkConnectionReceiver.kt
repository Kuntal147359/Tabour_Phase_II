package com.tabour.hospitality.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.tabour.hospitality.utils.NetworkUtil
import com.tabour.hospitality.viewmodels.SharedViewModel.Companion.connectivityStatus

class NetworkConnectionReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val status: Boolean = NetworkUtil.isInternetAvailable(context!!)
        Log.d("checknetwork", "Sulod sa network reciever ${status}")
        if ("android.net.conn.CONNECTIVITY_CHANGE" == intent!!.action) {
            connectivityStatus.value = status
        }

    }
}
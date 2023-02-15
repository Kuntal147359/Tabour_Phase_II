package com.tabour.hospitality.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.tabour.hospitality.base.NetworkConnectionReceiver


class NetworkUtil {

    val TYPE_WIFI = 1
    val TYPE_MOBILE = 2
    val TYPE_NOT_CONNECTED = 0
    val NETWORK_STATUS_NOT_CONNECTED = 0
    val NETWORK_STATUS_WIFI = 1
    val NETWORK_STATUS_MOBILE = 2

    companion object{

        fun isInternetAvailable(context: Context): Boolean{
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

    }

//    fun isConnectedToInternet(context: Context): Boolean {
//
//        try {
//            if (context != null) {
//                val connectivityManager =
//                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                val networkInfo = connectivityManager.activeNetworkInfo
//                return networkInfo != null && networkInfo.isConnected
//            }
//            return false
//        } catch (e: Exception) {
//            Log.e(
//                NetworkConnectionReceiver::class.java.name,
//                e.message!!
//            )
//            return false
//        }
//
//    }

}
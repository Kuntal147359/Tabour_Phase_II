package com.tabour.hospitality.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.onesignal.OneSignal
import com.tabour.hospitality.utils.ExampleNotificationOpenedHandler
import com.tabour.hospitality.viewmodels.SharedViewModel
import com.tabour.hospitality.worker.PracticeWorker
import java.util.*
import java.util.concurrent.TimeUnit

const val ONESIGNAL_APP_ID = "1b64a1ba-342c-4e51-a757-cda5fcd99a77"
class MyApplicationClass : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var mInstance: MyApplicationClass
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        @Synchronized
        fun getInstance(): MyApplicationClass {
            return mInstance
        }

        val TAG: String = MyApplicationClass::class.java
            .getSimpleName()

        fun getAppContext(): Context {
            return context!!
        }

        private var mRequestQueue: RequestQueue? = null

    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        mInstance = this

        // Disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        OneSignal.setNotificationOpenedHandler(ExampleNotificationOpenedHandler(this))

//        setUpWorker()
        // Set Localization
//        AppCommonUtils.setAppLocale(applicationContext, Locale.getDefault().language)

    }

//    private fun setUpWorker() {
//        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//        val workerRequest = PeriodicWorkRequest.Builder(PracticeWorker::class.java, 15, TimeUnit.MINUTES)
//            .setConstraints(constraint)
//            .build()
//        WorkManager.getInstance(this).enqueue(workerRequest)
//    }

    fun getRequestQueue(): RequestQueue? {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return mRequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue()!!.add(req)
    }


}
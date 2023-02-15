package com.tabour.hospitality.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tabour.hospitality.repository.LoginRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PracticeWorker(private val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {


        Log.d("checkworker", "Work manager called..")
        val loginRepo = LoginRepo.getInstance()

//        CoroutineScope(Dispatchers.IO).launch {
////            loginRepo.sendOtp(context,"919923032978")
//            loginRepo.sendOtp(context,"917775897339")
//        }

        return Result.success()

    }
}
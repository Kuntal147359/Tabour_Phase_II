package com.tabour.hospitality.utils

import android.app.ProgressDialog
import android.content.Context

class AppWaitDialog(context: Context) : ProgressDialog(context) {

    init {
        this.setMessage("Please wait...")
        this.setCancelable(false)
    }

}
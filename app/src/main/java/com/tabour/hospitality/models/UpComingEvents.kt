package com.tabour.hospitality.models

import android.graphics.Bitmap

data class UpComingEvents(

    var id: String,
    var mode: String,
    var title: String,
    var restro_name: String,
    var restro_address: String,
    var no_guest: String,
    var current_position: String,
    var current_waiting_time: String,
    var booking_date_time: String,
    var qr_url: String,
    var qrBitmap: Bitmap

)

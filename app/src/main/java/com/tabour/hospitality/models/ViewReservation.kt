package com.tabour.hospitality.models

import android.graphics.Bitmap

data class ViewReservation(
    val reservation_id: String,
    val res_name: String,
    val restro_address: String,
    val reserve_dttime: String,
    val no_guest: String,
    val occasion: String,
    val special_request: String,
    var qr_url: String,
    var timeout: String
)

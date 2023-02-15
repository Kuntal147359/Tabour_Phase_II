package com.tabour.hospitality.models

data class EshtablishmentDetails(
    var res_id: String,
    var res_name: String,
    var address: String,
    var latitude: String,
    var longitude: String,
    var description: String,
    var cuisine_type: String,
    var book_now_btn: String,
    var queue_btn: String,
    var is_saved: String,
    var seating_options: String,
    var imageList: ArrayList<String>,
    var openStatus: String,
    var closedAt: String,
    var parking: String,
    var workinghrsList: ArrayList<WorkingHours>

)

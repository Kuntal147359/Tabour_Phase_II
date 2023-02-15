package com.tabour.hospitality.models

data class NearbyRes(
    var id: Int,
    var name: String,
    var address: String,
    var cuisine_type: String,
    var book_now_btn: String,
    var queue_btn: String,
    var is_saved: String,
    var image: String,
    var avail_slots: ArrayList<String>
)

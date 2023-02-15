package com.tabour.hospitality.models

data class ChooseSeatingQueueData(
    var restro_id: String,
    var party_number: String,
    var postion_in_line: String,
    var waiting_time: String,
    var dining_area: ArrayList<Seating>
)

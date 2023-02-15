package com.tabour.hospitality.models

data class ViewQueueBookingData(
    var queueStatus: Boolean,
    var statusMsz: String,
    var restaurant_id: String,
    var user_queue_id: String,
    var initial_position: String,
    var initial_waiting_time: String,
    var restaurant_name: String,
    var restaurant_address: String,
    var allocated_time_slot: String,
    var booking_date: String,
    var partynumber: String,
    var qr_url: String,
    var timeout: String
)

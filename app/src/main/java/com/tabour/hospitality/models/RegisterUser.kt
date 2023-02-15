package com.tabour.hospitality.models

data class RegisterUser(
    var fname: String,
    var lname: String,
    var email: String,
    var country_phonecode: String,
    var mobile: String
)

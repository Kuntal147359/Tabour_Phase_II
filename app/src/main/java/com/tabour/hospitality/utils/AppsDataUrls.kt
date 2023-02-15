package com.tabour.hospitality.utils

class AppsDataUrls {

    companion object {
//        val BaseUrl = "http://103.252.168.24/tabour/api/user"
        val BaseUrl = "https://tabour.qa/api/user"

        fun getCountriesUrl(): String {
            return "$BaseUrl/country_data"
        }

        fun sendOTPUrl(): String {
            return "$BaseUrl/sendotp"
        }

        fun verifyUserUrl(): String {
            return "$BaseUrl/after_otp_varification"
        }

        fun registerUser(): String {
            return "$BaseUrl/register"
        }

        fun loginUserUrl(): String {
            return "$BaseUrl/login"
        }

        fun getAllCatUrl(): String {
            return "$BaseUrl/all_food_categories"
        }

        fun getUpcmgevntsUrl(): String {
            return "$BaseUrl/upcomming_events"
        }

        fun getExploreResUrl(): String {
            return "$BaseUrl/explore_home_restaurants"
        }

        fun getAvailableNowResUrl(): String {
            return "$BaseUrl/available_home_restaurants"
        }

        fun getNearbyResUrl(): String {
            return "$BaseUrl/nearby_restaurants"
        }

        fun saveEshtblshmntUrl(): String {
            return "$BaseUrl/save_establishment"
        }

        fun unSaveEshtblshmntUrl(): String {
            return "$BaseUrl/unsave_establishment"
        }

        fun getLogoutUrl(): String {
            return "$BaseUrl/user_log_out"
        }

        fun getDeactivateAccountUrl(): String {
            return "$BaseUrl/user_account_deactivate"
        }

        fun searchEshtablishmentUrl(): String {
            return "$BaseUrl/restaurants_filter"
        }

        fun restaurantsDtlsUrl(): String {
            return "$BaseUrl/establishment_details"
        }

        fun getrstrotmbydtUrl(): String {
            return "$BaseUrl/restro_slots"
        }

        fun getgetrestrodinareasUrl(): String {
            return "$BaseUrl/restro_dining_areas"
        }

        fun submitBookingUrl(): String {
            return "$BaseUrl/book_table"
        }

        fun checkUserQueueUrl(): String {
            return "$BaseUrl/check_user_is_in_queue"
        }

        fun queueBookingUrl(): String {
            return "$BaseUrl/queue_booking"
        }

        fun viewBookingQueueUrl(): String {
            return "$BaseUrl/enter_in_queue"
        }

        fun viewQueueDetailsUrl(): String {
            return "$BaseUrl/view_queue_details"
        }

        fun reservationHistoryUrl(): String {
            return "$BaseUrl/reservation_history"
        }

        fun uncomingReservationListUrl(): String {
            return "$BaseUrl/upcoming_reservation"
        }

        fun viewReservationUrl(): String {
            return "$BaseUrl/view_reservation"
        }

        fun cancelReservation(): String {
            return "$BaseUrl/cancel_reservation"
        }

        fun ontheWay(): String {
            return "$BaseUrl/queue_im_on_the_way"
        }

        fun leaveQueueUrl(): String {
            return "$BaseUrl/leave_queue"
        }

        fun getProfileUrl(): String {
            return "$BaseUrl/user_profile"
        }

        fun updateProfileImage(): String {
            return "$BaseUrl/update_profile_pic"
        }

        fun updateProfileDetails(): String {
            return "$BaseUrl/user_profile_update"
        }

        fun getRestroMenuitemsUrl(): String {
            return "$BaseUrl/custom_menu"
        }

        fun getSavedResListUrl(): String {
            return "$BaseUrl/saved_establishment"
        }

        fun getBookagainResUrl(): String {
            return "$BaseUrl/book_again_restaurants"
        }

        fun getNotificationsUrl(): String {
            return "$BaseUrl/notifications"
        }

        fun getReadNotificationStatusUrl(): String{
            return "$BaseUrl/check_notifications"
        }

    }
}
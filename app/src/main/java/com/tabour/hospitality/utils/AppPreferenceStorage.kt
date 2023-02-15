package com.tabour.hospitality.utils

import android.content.Context
import android.content.SharedPreferences
import com.tabour.hospitality.base.MyApplicationClass

class AppPreferenceStorage {


    companion object{
        private val mAppPref = "mAppPref"

        private var USERID = ""
        private var PLAYERID = ""
        private var USERNAME = ""
        private var EMAIL = ""
        private var PROFILE_PIC = ""
        private var AUTHTOKEN = ""
        private var PHONE = ""
        private var COUNT = ""
        private var USER_ROLE = 0
        private var OTP = 0
        private var VERIFIED_STATUS = 0
        private var FEATURES = ""
        private var PACKAGE_STATUS = ""

        // Set UserId
        fun saveUserid(userid: String){

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            val editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("USERID", userid)
            setUserid(userid)
            editor.apply()

        }

        fun getUserid(): String{

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            USERID = hxPrefs.getString("USERID", "")!!

            return USERID

        }

        fun setUserid(userid: String){

            USERID = userid

        }

        // Set PlayerId
        fun savePlayerid(playerid: String){

            var hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            var editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("PLAYERID", playerid)
            setPlayerid(playerid)
            editor.apply()

        }

        fun getPlayerid(): String{

            var hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            PLAYERID = hxPrefs.getString("PLAYERID", null).toString()

            return PLAYERID

        }

        fun setPlayerid(playerid: String){

            PLAYERID = playerid

        }

        // Set AuthToken
        fun saveAuthToken(authtoken: String){

            var hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            var editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("AUTHTOKEN", authtoken)
            setAuthToken(authtoken)
            editor.apply()

        }

        fun getAuthToken(): String{

            var hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            AUTHTOKEN = hxPrefs.getString("AUTHTOKEN", null).toString()

            return AUTHTOKEN

        }

        fun setAuthToken(authtoken: String){

            AUTHTOKEN = authtoken

        }

        // Save username
        fun saveUsername(username: String){

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            val editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("USERNAME", username)
            setUsername(username)
            editor.apply()

        }

        fun getUsername(): String{

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            USERNAME = hxPrefs.getString("USERNAME", "")!!

            return USERNAME

        }

        fun setUsername(username: String){

            USERNAME = username

        }

        // Save mobile number
        fun saveMobileNum(phone: String){

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            val editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("PHONE", phone)
            setMobileNum(phone)
            editor.apply()

        }

        fun getMobileNum(): String{

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            PHONE = hxPrefs.getString("PHONE", "")!!

            return PHONE

        }

        fun setMobileNum(phone: String){

            PHONE = phone

        }

        // Save mobile number
        fun saveProfilePic(pic: String){

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            val editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("PROFILE_PIC", pic)
            setProfilePic(pic)
            editor.apply()

        }

        fun getProfilePic(): String{

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            PROFILE_PIC = hxPrefs.getString("PROFILE_PIC", "")!!

            return PROFILE_PIC

        }

        fun setProfilePic(pic: String){

            PROFILE_PIC = pic

        }

        // Save Guest Count
        fun saveCount(count: String){

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )

            val editor: SharedPreferences.Editor = hxPrefs.edit()
            editor.putString("COUNT", count)
            setCount(count)
            editor.apply()

        }

        fun getCount(): String{

            val hxPrefs: SharedPreferences = MyApplicationClass.getAppContext().getSharedPreferences(
                mAppPref, Context.MODE_PRIVATE
            )
            COUNT = hxPrefs.getString("COUNT", "")!!

            return COUNT

        }

        fun setCount(count: String){

            COUNT = count

        }



    }
}
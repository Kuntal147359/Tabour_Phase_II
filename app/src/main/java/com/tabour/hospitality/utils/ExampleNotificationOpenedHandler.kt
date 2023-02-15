package com.tabour.hospitality.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal
import com.tabour.hospitality.activity.LoginActivity
import com.tabour.hospitality.activity.MainActivity
import com.tabour.hospitality.fragments.NotificationFragment
import org.json.JSONObject

class ExampleNotificationOpenedHandler(val context: Context): OneSignal.OSNotificationOpenedHandler  {

    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        val actionType = result!!.action.type
        val data: JSONObject = result.notification.additionalData
        val category: String

        Log.e("OneSignalExample", "data is: " + data.toString())

//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//        context.startActivity(intent)

        category = data.getString("category")
        category?.let {
            when(category)
            {
                "1" -> {
                    Log.e("OneSignalExample", "category value: ${category}")

                    val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("mode","notification")
                        intent.putExtra("category","1")
                        intent.putExtra("booking_id","")
                        context.startActivity(intent)

//                    if(AppCommonUtils.getCurrentActivity(context).contains("MainActivity"))
//                    {
//                        AppCommonUtils.loadFragment(context, NotificationFragment())
//                    }
//                    else
//                    {
//                        val intent = Intent(context, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent.putExtra("mode","notification")
//                        intent.putExtra("category","1")
//                        context.startActivity(intent)
//                    }
                }
                "2" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("mode","notification")
                    intent.putExtra("category","2")
                    intent.putExtra("booking_id","")
                    context.startActivity(intent)
                    Log.e("OneSignalExample", "category value: announcement")
                }
                "3" -> {

                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("mode","notification")
                    intent.putExtra("category","3")
                    intent.putExtra("booking_id",data.getString("booking_id"))
                    context.startActivity(intent)
                    Log.e("OneSignalExample", "category value: ${category}")
                }
                "4" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("mode","notification")
                    intent.putExtra("category","4")
                    intent.putExtra("booking_id",data.getString("booking_id"))
                    context.startActivity(intent)
                    Log.e("OneSignalExample", "category value: queue")
                }
                "5" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("mode","notification")
                    intent.putExtra("category","5")
                    intent.putExtra("booking_id","")
                    context.startActivity(intent)
                    Log.e("OneSignalExample", "category value: ${category}")
                }
                else -> {

                }
            }

        }

//        if (actionType == OSNotificationAction.ActionType.ActionTaken)
//        {
//            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionId)
//
//        }

//        OSNotificationOpenedResult{
//            notification= OSNotification{
//                notificationExtender=null, groupedNotifications=[], androidNotificationId=1152458340, notificationId='40f27249-b9cb-4e72-8abf-45f8af8d1b16', templateName='', templateId='', title='null', body='fgfsdg', additionalData=
//                {"Subject":"New Offer!!!"}, smallIcon='null', largeIcon='null', bigPicture='null', smallIconAccentColor='null', launchURL='null', sound='null', ledColor='null', lockScreenVisibility=1, groupKey='null', groupMessage='null', actionButtons=null, fromProjectNumber='438424276362', backgroundImageLayout=null, collapseId='null', priority=0, rawPayload='{"google.delivered_priority":"normal","google.sent_time":1673935823049,"google.ttl":259200,"google.original_priority":"normal","custom":"{\"a\":{\"Subject\":\"New Offer!!!\"},\"i\":\"40f27249-b9cb-4e72-8abf-45f8af8d1b16\"}","from":"438424276362","alert":"fgfsdg","google.message_id":"0:1673935823083612%c98f05eaf9fd7ecd","google.c.sender.id":"438424276362","androidNotificationId":1152458340}'}, action=com.onesignal.OSNotificationAction@b376a73, isComplete=true}


    }

}
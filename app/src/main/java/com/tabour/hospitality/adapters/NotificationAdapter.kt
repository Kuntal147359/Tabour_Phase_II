package com.tabour.hospitality.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ViewBooking
import com.tabour.hospitality.fragments.ViewQueueBooking
import com.tabour.hospitality.models.HistryBookng
import com.tabour.hospitality.models.Notification_model
import com.tabour.hospitality.utils.AppCommonUtils

class NotificationAdapter(
    context: Context,
    activity: Activity,
    notificationList: ArrayList<Notification_model>
): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    var context: Context
    var activity: Activity
    var notificationList: ArrayList<Notification_model>
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.activity = activity
        this.notificationList = notificationList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var img_notification_category: ImageView = itemView.findViewById(R.id.img_notification_category)
        var tv_notification_details: TextView = itemView.findViewById(R.id.tv_notification_details)
        var tv_notification_time: TextView = itemView.findViewById(R.id.tv_notification_time)
        var divider_view: View = itemView.findViewById(R.id.divider_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = mInflater.inflate(R.layout.notification_row, parent, false)
        return NotificationViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        with(holder)
        {
            tv_notification_details.text = notificationList.get(position).text
            tv_notification_time.text = notificationList.get(position).notification_time

            when(notificationList.get(position).category)
            {
                "announcement" -> {
                    img_notification_category.setImageDrawable(context.getDrawable(R.drawable.annoucement_128))
                }
                "offer" -> {
                    img_notification_category.setImageDrawable(context.getDrawable(R.drawable.offer_128))
                }
                "reservation" -> {
                    img_notification_category.setImageDrawable(context.getDrawable(R.drawable.reservation_128))
                }
                "queue" -> {
                    img_notification_category.setImageDrawable(context.getDrawable(R.drawable.queue_128))
                }
                "other" -> {
                    img_notification_category.setImageDrawable(context.getDrawable(R.drawable.others))
                }
            }

            if(position == notificationList.size - 1)
            {
                divider_view.visibility = View.GONE
            }
            else
            {
                divider_view.visibility = View.VISIBLE
            }

            lyt_parent.setOnClickListener {
                when(notificationList.get(position).category)
                {
                    "reservation" -> {
                        AppCommonUtils.hideKeyboard(context)
                        val bundle = Bundle()
                        bundle.putString("reservation_id",notificationList.get(position).cat_id)
                        bundle.putString("category", "1")
                        val viewBooking = ViewBooking()
                        viewBooking.arguments = bundle
                        AppCommonUtils.loadFragment(activity, viewBooking)
                    }
                    "queue" -> {
                        AppCommonUtils.hideKeyboard(context)
                        val bundle = Bundle()
                        bundle.putString("mode","user_in_queue")
                        bundle.putString("category", "1")
                        bundle.putString("user_queue_id",notificationList.get(position).cat_id)
                        val viewQueueBooking = ViewQueueBooking()
                        viewQueueBooking.arguments = bundle
                        AppCommonUtils.loadFragment(activity, viewQueueBooking)
                    }
                }
            }


        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

}
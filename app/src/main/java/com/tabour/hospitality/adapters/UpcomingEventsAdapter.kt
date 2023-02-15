package com.tabour.hospitality.adapters

import android.app.Activity
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ViewBooking
import com.tabour.hospitality.fragments.ViewQueueBooking
import com.tabour.hospitality.models.UpComingEvents
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppCommonUtils.Companion.getQrCodeBitmap

class UpcomingEventsAdapter(
    activity: Activity,
    context: Context,
    upcomingEventsViewpager: ViewPager2,
    upcomingEventsList: ArrayList<UpComingEvents>
) : RecyclerView.Adapter<UpcomingEventsAdapter.UpcomingEventsViewHolder>() {

    var activity: Activity
    var context: Context
    var upcomingEventsList: ArrayList<UpComingEvents>
    var upcomingEventsViewpager: ViewPager2
    var mInflater: LayoutInflater
    private val VIEW_TYPE_RESERVATION = 1
    private val VIEW_TYPE_QUEUE_BOOKING = 2

    init {
        this.activity = activity
        this.context = context
        this.upcomingEventsList = upcomingEventsList
        this.upcomingEventsViewpager = upcomingEventsViewpager
        this.mInflater = LayoutInflater.from(context)
    }

    override fun getItemViewType(position: Int): Int {

        val upComingEvents = upcomingEventsList.get(position)

        if (upComingEvents.mode.equals("1")) {
            return VIEW_TYPE_RESERVATION
        } else {
            return VIEW_TYPE_QUEUE_BOOKING
        }
    }

    inner class UpcomingEventsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val tv_booking_type: TextView = itemView.findViewById(R.id.tv_booking_type)
        val tv_rest_name: TextView = itemView.findViewById(R.id.tv_rest_name)
        val tv_rest_address: TextView = itemView.findViewById(R.id.tv_rest_address)
        val qr_image: ImageView = itemView.findViewById(R.id.qr_image)

        fun booking(upComingEvents: UpComingEvents){
            val lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
            val tv_no_guest: TextView = itemView.findViewById(R.id.tv_no_guest)
            val tv_bookiong_date_time: TextView = itemView.findViewById(R.id.tv_bookiong_date_time)
            tv_booking_type.text = upComingEvents.title
            tv_rest_name.text = upComingEvents.restro_name
            tv_rest_address.text = upComingEvents.restro_address
            tv_no_guest.text = upComingEvents.no_guest
            tv_bookiong_date_time.text = upComingEvents.booking_date_time
            qr_image.setImageBitmap(upComingEvents.qrBitmap)
            lyt_parent.setOnClickListener {
                AppCommonUtils.hideKeyboard(activity)
                val bundle = Bundle()
                bundle.putString("reservation_id", upComingEvents.id)
                bundle.putString("category", "1")
                val viewBooking = ViewBooking()
                viewBooking.arguments = bundle
                AppCommonUtils.loadFragment(activity, viewBooking)
            }
        }

        fun queueBooking(upComingEvents: UpComingEvents){
            val lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
            val tv_people_inline: TextView = itemView.findViewById(R.id.tv_people_inline)
            val tv_est_msz: TextView = itemView.findViewById(R.id.tv_est_msz)
            tv_booking_type.text = upComingEvents.title
            tv_rest_name.text = upComingEvents.restro_name
            tv_rest_address.text = upComingEvents.restro_address
            tv_people_inline.text = upComingEvents.current_position
            tv_est_msz.text = "people in line | ${upComingEvents.current_waiting_time} min est time"
            qr_image.setImageBitmap(upComingEvents.qrBitmap)
            lyt_parent.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("mode","user_in_queue")
                bundle.putString("category", "1")
                bundle.putString("restro_id",upComingEvents.id)
                bundle.putString("dinning_area_id","")
                bundle.putString("user_queue_id",upComingEvents.id)
                val viewQueueBooking = ViewQueueBooking()
                viewQueueBooking.arguments = bundle
                AppCommonUtils.loadFragment(activity,viewQueueBooking)
            }
        }

        fun bind(upComingEvents: UpComingEvents, viwetype: Int) {
            when (viwetype) {
                1 -> {
                    booking(upComingEvents)
                }
                2 -> {
                    queueBooking(upComingEvents)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventsAdapter.UpcomingEventsViewHolder {

        val layout = when(viewType){
            1 -> {
                R.layout.upcoming_reservation_row
            }
            2 -> {
                R.layout.notify_bookqueue_row
            }
            else -> {
                throw IllegalArgumentException("Invalid type")
            }
        }

        val itemView = mInflater.inflate(layout, parent, false)
        return UpcomingEventsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UpcomingEventsAdapter.UpcomingEventsViewHolder, position: Int) {
        val upComingEvents = upcomingEventsList.get(position)
        with(holder)
        {
            tv_booking_type.text = upcomingEventsList.get(position).title
            tv_rest_name.text = upcomingEventsList.get(position).restro_name
            tv_rest_address.text = upcomingEventsList.get(position).restro_address

            when (holder.itemViewType) {
                VIEW_TYPE_RESERVATION -> holder.bind(upComingEvents, VIEW_TYPE_RESERVATION)
                VIEW_TYPE_QUEUE_BOOKING -> holder.bind(upComingEvents, VIEW_TYPE_QUEUE_BOOKING)
            }
        }
    }

    override fun getItemCount(): Int {
        return upcomingEventsList.size
    }

}
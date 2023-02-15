package com.tabour.hospitality.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ViewBooking
import com.tabour.hospitality.fragments.ViewQueueBooking
import com.tabour.hospitality.models.HistryBookng
import com.tabour.hospitality.utils.AppCommonUtils


class BookinHistoryAdapter(activity: Activity, context: Context, histryBookngList: ArrayList<HistryBookng>): RecyclerView.Adapter<BookinHistoryAdapter.BookinHistoryViewHolder>() {
    var context: Context
    var activity: Activity
    var histryBookngList: ArrayList<HistryBookng>
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.activity = activity
        this.histryBookngList = histryBookngList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class BookinHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var tv_rest_name: TextView = itemView.findViewById(R.id.tv_rest_name)
        var tv_rest_address: TextView = itemView.findViewById(R.id.tv_rest_address)
        var img_save: ImageView = itemView.findViewById(R.id.img_save)
        var tv_resvtn_date: TextView = itemView.findViewById(R.id.tv_resvtn_date)
        var image_restaurant: ImageView = itemView.findViewById(R.id.image_restaurant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookinHistoryViewHolder {
        val itemView = mInflater.inflate(R.layout.reservation_history_row, parent, false)
        return BookinHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookinHistoryViewHolder, position: Int) {
        Glide.with(context)
            .load(histryBookngList.get(position).restro_image)
            .into(holder.image_restaurant)

        if(histryBookngList.get(position).is_saved.equals("0"))
        {
            holder.img_save.setImageResource(R.drawable.save_yellow)
        }
        else
        {
            holder.img_save.setImageResource(R.drawable.save_icon)
        }

        holder.tv_rest_name.text = histryBookngList.get(position).restro_name
        holder.tv_rest_address.text = histryBookngList.get(position).restro_address
        holder.tv_resvtn_date.text = histryBookngList.get(position).booking_date_time
        holder.lyt_parent.setOnClickListener {

            if(histryBookngList.get(position).booking_status.equals("0"))
            {
                Toast.makeText(context, context.getString(R.string.bokking_already_canceled), Toast.LENGTH_LONG).show()
            }
            else{
                if(histryBookngList.get(position).reservation_type.equals("RESERVATION"))
                {
                    AppCommonUtils.hideKeyboard(activity)
                    val bundle = Bundle()
                    bundle.putString("reservation_id", histryBookngList.get(position).id)
                    bundle.putString("category", histryBookngList.get(position).mode)
                    val viewBooking = ViewBooking()
                    viewBooking.arguments = bundle
                    AppCommonUtils.loadFragment(activity, viewBooking)
                }
                else{
                    val bundle = Bundle()
//                    mode.equals("user_in_queue")
                    bundle.putString("mode","user_in_queue")
                    bundle.putString("category", histryBookngList.get(position).mode)
                    bundle.putString("user_queue_id",histryBookngList.get(position).id)
                    val viewQueueBooking = ViewQueueBooking()
                    viewQueueBooking.arguments = bundle
                    AppCommonUtils.loadFragment(activity,viewQueueBooking)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return histryBookngList.size
    }
}
package com.tabour.hospitality.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ChooseseatingQueueFragment
import com.tabour.hospitality.fragments.ResdetailsFragment
import com.tabour.hospitality.fragments.ReservationFragment
import com.tabour.hospitality.models.AvailableNow
import com.tabour.hospitality.models.NearbyRes
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.ItemClickListener

class AvailableNowAdapter(
    activity: Activity,
    context: Context,
    avalnowResList: ArrayList<AvailableNow>
) : RecyclerView.Adapter<AvailableNowAdapter.AvailableNowViewHolder>() {

    var activity: Activity
    var context: Context
    var avalnowResList: ArrayList<AvailableNow>
    lateinit var clickListener: ItemClickListener
    var mInflater: LayoutInflater

    init {
        this.activity = activity
        this.context = context
        this.avalnowResList = avalnowResList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class AvailableNowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var img_save: ImageView = itemView.findViewById(R.id.img_save)
        var lyt_book_now: LinearLayout = itemView.findViewById(R.id.lyt_book_now)
        var lyt_join_queue: LinearLayout = itemView.findViewById(R.id.lyt_join_queue)
        var image_restaurant: ImageView = itemView.findViewById(R.id.image_restaurant)
        var tv_rest_name: TextView = itemView.findViewById(R.id.tv_rest_name)
        var tv_rest_address: TextView = itemView.findViewById(R.id.tv_rest_address)
        var tv_cuisine_type: TextView = itemView.findViewById(R.id.tv_cuisine_type)
        var slots_recy: RecyclerView = itemView.findViewById(R.id.slots_recy)
        var tv_no_slots_available: TextView = itemView.findViewById(R.id.tv_no_slots_available)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableNowViewHolder {
        val itemView = mInflater.inflate(R.layout.availnow_res_row, parent, false)
        return AvailableNowViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: AvailableNowViewHolder, position: Int) {

        with(holder) {
            // For restaurant images
            Glide.with(context)
                .load(avalnowResList.get(position).image)
                .into(image_restaurant)

            tv_rest_name.text = avalnowResList.get(position).name
            tv_rest_name.text = avalnowResList.get(position).name
            tv_rest_address.text = avalnowResList.get(position).address
            tv_cuisine_type.text = avalnowResList.get(position).cuisine_type

            if (avalnowResList.get(position).book_now_btn.equals("0")) {
                lyt_book_now.visibility = View.GONE
            } else {
                lyt_book_now.visibility = View.VISIBLE
            }

            if (avalnowResList.get(position).queue_btn.equals("0")) {
//            holder.lyt_book_now.visibility = View.VISIBLE
                lyt_join_queue.visibility = View.GONE
            } else {
//            holder.lyt_book_now.visibility = View.GONE
                lyt_join_queue.visibility = View.VISIBLE
            }

            if (avalnowResList.get(position).is_saved.equals("0")) {
                img_save.setImageResource(R.drawable.unsave_icon)
            } else {
                img_save.setImageResource(R.drawable.save_icon)
            }

            lyt_parent.setOnClickListener { view ->
                AppCommonUtils.hideKeyboard(context)

                clickListener.let {
                    clickListener.onClick(view, "Availableresdtls", position)
                }
            }

            lyt_book_now.setOnClickListener { view ->
                AppCommonUtils.hideKeyboard(activity)

                clickListener.let {
                    clickListener.onClick(view, "Book_now_available", position)
                }
            }

            if (avalnowResList.get(position).avail_slots.size > 0) {
                slots_recy.visibility = View.VISIBLE
                tv_no_slots_available.visibility = View.GONE
                val slotsAdapter = SlotsAdapter(
                    activity,
                    context,
                    avalnowResList.get(position).avail_slots
                )

                //expertListRecyclerView.setHasFixedSize(true)
                slots_recy.smoothScrollToPosition(0)
                slots_recy.setLayoutManager(
                    LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                slots_recy.setAdapter(slotsAdapter)
                slotsAdapter.notifyDataSetChanged()
            } else {
                slots_recy.visibility = View.GONE
                tv_no_slots_available.visibility = View.VISIBLE
            }

            lyt_join_queue.setOnClickListener { view ->

                clickListener.let {
                    clickListener.onClick(
                        view,
                        "checkqueueAvail",
                        position
                    ) // call the onClick in the OnItemClickListener
                }
            }

        }
    }

    @JvmName("setClickListener1")
    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return avalnowResList.size
    }

}
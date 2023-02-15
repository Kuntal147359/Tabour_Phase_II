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
import android.widget.StackView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ChooseseatingQueueFragment
import com.tabour.hospitality.fragments.ResdetailsFragment
import com.tabour.hospitality.fragments.ReservationFragment
import com.tabour.hospitality.models.NearbyRes
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.ItemClickListener


class NearbyResAdapter(activity: Activity, context: Context, nearbyResList: ArrayList<NearbyRes>): RecyclerView.Adapter<NearbyResAdapter.NearbyViewHolder>() {

    var activity: Activity
    var context: Context
    var nearbyResList: ArrayList<NearbyRes>
    lateinit var clickListener: ItemClickListener
    var mInflater: LayoutInflater

    init {
        this.activity = activity
        this.context = context
        this.nearbyResList = nearbyResList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class NearbyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyViewHolder {
        val itemView = mInflater.inflate(R.layout.nearby_res_row, parent, false)
        return NearbyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: NearbyViewHolder, position: Int) {
        // For restaurant images
        Glide.with(context)
            .load(nearbyResList.get(position).image)
            .into(holder.image_restaurant)

        holder.tv_rest_name.text = nearbyResList.get(position).name
        holder.tv_rest_address.text = nearbyResList.get(position).address
        holder.tv_cuisine_type.text = nearbyResList.get(position).cuisine_type

        if(nearbyResList.get(position).book_now_btn.equals("0"))
        {
            holder.lyt_book_now.visibility = View.GONE
        }
        else
        {
            holder.lyt_book_now.visibility = View.VISIBLE
        }

        if(nearbyResList.get(position).queue_btn.equals("0"))
        {
//            holder.lyt_book_now.visibility = View.VISIBLE
            holder.lyt_join_queue.visibility = View.GONE
        }
        else
        {
//            holder.lyt_book_now.visibility = View.GONE
            holder.lyt_join_queue.visibility = View.VISIBLE
        }

        if(nearbyResList.get(position).is_saved.equals("0"))
        {
            holder.img_save.setImageResource(R.drawable.unsave_icon)
        }
        else
        {
            holder.img_save.setImageResource(R.drawable.save_icon)
        }

        holder.lyt_parent.setOnClickListener {view ->
            AppCommonUtils.hideKeyboard(context)

            clickListener.let {
                clickListener.onClick(view,"NearByresdtls",position)
            }
        }

        holder.lyt_book_now.setOnClickListener {view ->
            AppCommonUtils.hideKeyboard(activity)

            clickListener.let {
                clickListener.onClick(view,"Book_now_nearby",position)
            }
        }

        if(nearbyResList.get(position).avail_slots.size > 0)
        {
            holder.slots_recy.visibility = View.VISIBLE
            holder.tv_no_slots_available.visibility = View.GONE
            val slotsAdapter = SlotsAdapter(
                activity,
                context,
                nearbyResList.get(position).avail_slots
            )

            //expertListRecyclerView.setHasFixedSize(true)
            holder.slots_recy.smoothScrollToPosition(0)
            holder.slots_recy.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            holder.slots_recy.setAdapter(slotsAdapter)
            slotsAdapter.notifyDataSetChanged()
        }
        else
        {
            holder.slots_recy.visibility = View.GONE
            holder.tv_no_slots_available.visibility = View.VISIBLE
        }

        holder.lyt_join_queue.setOnClickListener { view ->

            clickListener.let {
                clickListener.onClick(view,"checkqueue", position) // call the onClick in the OnItemClickListener
            }

//            if(AppPreferenceStorage.getUserid().equals(""))
//            {
//                AppCommonUtils.showCancelBookingDialog(activity)
//            }
//            else{
//                val bundle = Bundle()
//                bundle.putString("restro_id",nearbyResList.get(position).id.toString())
//                val chooseseatingQueueFragment = ChooseseatingQueueFragment()
//                chooseseatingQueueFragment.arguments = bundle
//                AppCommonUtils.loadFragment(activity,chooseseatingQueueFragment)
//            }
        }
    }

    @JvmName("setClickListener1")
    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return nearbyResList.size
    }

}
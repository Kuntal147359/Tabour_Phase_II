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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ChooseseatingQueueFragment
import com.tabour.hospitality.fragments.ResdetailsFragment
import com.tabour.hospitality.fragments.ReservationFragment
import com.tabour.hospitality.models.SavedRestaurant
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.utils.ItemClickListener
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SharedViewModel.Companion.selectedDate

class SavedResAdapter(
    activity: Activity,
    context: Context,
    svdresList: ArrayList<SavedRestaurant>
): RecyclerView.Adapter<SavedResAdapter.SavedResViewHolder>() {

    var context: Context
    var activity: Activity
    var svdresList: ArrayList<SavedRestaurant>
    lateinit var clickListener: ItemClickListener
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.activity = activity
        this.svdresList = svdresList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class SavedResViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedResViewHolder {
        val itemView = mInflater.inflate(R.layout.savedres_row, parent, false)
        return SavedResViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SavedResViewHolder, position: Int) {
        // For restaurant images
        Glide.with(context)
            .load(svdresList.get(position).image)
            .into(holder.image_restaurant)

        holder.tv_rest_name.text = svdresList.get(position).name
        holder.tv_rest_address.text = svdresList.get(position).address
        holder.tv_cuisine_type.text = svdresList.get(position).cuisine_type

        if(svdresList.get(position).book_now_btn.equals("0"))
        {
            holder.lyt_book_now.visibility = View.GONE
        }
        else
        {
            holder.lyt_book_now.visibility = View.VISIBLE
        }

        if(svdresList.get(position).queue_btn.equals("0"))
        {
//            holder.lyt_book_now.visibility = View.VISIBLE
            holder.lyt_join_queue.visibility = View.GONE
        }
        else
        {
//            holder.lyt_book_now.visibility = View.GONE
            holder.lyt_join_queue.visibility = View.VISIBLE
        }

        if(svdresList.get(position).is_saved.equals("0"))
        {
            holder.img_save.setImageResource(R.drawable.unsave_icon)
        }
        else
        {
            holder.img_save.setImageResource(R.drawable.save_icon)
        }


        holder.lyt_parent.setOnClickListener {
            AppCommonUtils.hideKeyboard(context)
            val bundle = Bundle()
            bundle.putString("res_id",svdresList.get(position).id.toString())
            bundle.putString("sldt", "${DateToStringConversion.getCurrentMonth().first} ${DateToStringConversion.getCurrentMonth().second}")
            bundle.putString("selectedDate", "")
            bundle.putString("selectedTime", "")
            val resdetailsFragment = ResdetailsFragment()
            resdetailsFragment.arguments = bundle
            AppCommonUtils.loadFragment(activity, resdetailsFragment)
        }

        holder.lyt_book_now.setOnClickListener {view ->
            AppCommonUtils.hideKeyboard(activity)

            if(AppPreferenceStorage.getUserid().equals(""))
            {
                AppCommonUtils.showCancelBookingDialog(activity)
            }
            else
            {
                val bundle = Bundle()
                bundle.putString("restro_id", svdresList.get(position).id.toString())
                bundle.putString("restro_name", svdresList.get(position).name.toString())
                bundle.putString("restro_address", svdresList.get(position).address.toString())
                bundle.putString("sldt", "${DateToStringConversion.getCurrentMonth().first} ${DateToStringConversion.getCurrentMonth().second}")
                bundle.putString("selectedDate", "")
                bundle.putString("selectedTime", "")
                val reservationFragment = ReservationFragment()
                reservationFragment.arguments = bundle
                AppCommonUtils.loadFragment(activity, reservationFragment)
            }
        }

        if(svdresList.get(position).avail_slots.size > 0)
        {
            holder.slots_recy.visibility = View.VISIBLE
            holder.tv_no_slots_available.visibility = View.GONE
            val slotsAdapter = SlotsAdapter(
                activity,
                context,
                svdresList.get(position).avail_slots
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
                clickListener.onClick(view,"checkqueueAvail", position) // call the onClick in the OnItemClickListener
            }

//            if(AppPreferenceStorage.getUserid().equals(""))
//            {
//                AppCommonUtils.showCancelBookingDialog(activity)
//            }
//            else
//            {
//                val bundle = Bundle()
//                bundle.putString("restro_id",svdresList.get(position).id.toString())
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
        return svdresList.size
    }
}
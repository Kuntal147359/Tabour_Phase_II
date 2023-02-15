package com.tabour.hospitality.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.TimeSlot
import com.tabour.hospitality.utils.ItemClickListener

class TimeSlotByDateAdapter(context: Context, slotList: ArrayList<TimeSlot>, selectedTime: String) :
    RecyclerView.Adapter<TimeSlotByDateAdapter.TimeSlotByDateViewHolder>() {

    var context: Context
    var slotList: ArrayList<TimeSlot>
    var selectedTime: String
    var mInflater: LayoutInflater
    lateinit var clickListener: ItemClickListener
    var selectedPosition = -1
    var defaultSelection = 0

    init {
        this.context = context
        this.slotList = slotList
        this.selectedTime = selectedTime
        this.mInflater = LayoutInflater.from(context)
    }

    inner class TimeSlotByDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_time: TextView
        var txt_time_format: TextView
        var lyt_parent: LinearLayout
        init {
            txt_time = itemView.findViewById(R.id.txt_time)
            txt_time_format = itemView.findViewById(R.id.txt_time_format)
            lyt_parent = itemView.findViewById(R.id.lyt_parent)
        }

//        override fun onClick(view: View?) {
//            clickListener.let {
//                clickListener.onClick(view, getLayoutPosition()); // call the onClick in the OnItemClickListener
//            }
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotByDateViewHolder {
        val itemView = mInflater.inflate(R.layout.tmslotsbydate_row, parent, false)
        return TimeSlotByDateViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TimeSlotByDateViewHolder, position: Int) {

        if(defaultSelection == 0)
        {
            val comp_a = "${slotList.get(position).dsply_time} ${slotList.get(position).dsply_format}"

            if(selectedTime.equals(comp_a,ignoreCase = true))
            {
                selectedPosition = position
                defaultSelection = 1

                clickListener.let {
                    clickListener.onClick(holder.lyt_parent,"timeslot", position)
                }

            }
        }

        if(selectedPosition==position)
        {
            holder.txt_time.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.txt_time_format.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.lyt_parent.setBackgroundResource(R.drawable.bezier_curve_filled_rectangle)
        }
        else
        {
            holder.txt_time.setTextColor(ContextCompat.getColor(context,R.color.h_text_color))
            holder.txt_time_format.setTextColor(ContextCompat.getColor(context,R.color.h_text_color))
            holder.lyt_parent.setBackgroundColor(ContextCompat.getColor(context,R.color.lyt_explore_bg_color))
        }

        holder.txt_time.text = slotList.get(position).dsply_time
        holder.txt_time_format.text = slotList.get(position).dsply_format
        holder.lyt_parent.setOnClickListener { view ->
            selectedPosition = position
            notifyDataSetChanged()
            clickListener.let {
                clickListener.onClick(view,"timeslot", position) // call the onClick in the OnItemClickListener
            }
        }
    }

    @JvmName("setClickListener1")
    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return slotList.size
    }

}
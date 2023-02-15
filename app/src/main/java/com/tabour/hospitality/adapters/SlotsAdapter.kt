package com.tabour.hospitality.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.NearbyRes

class SlotsAdapter(activity: Activity, context: Context, slotList: ArrayList<String>): RecyclerView.Adapter<SlotsAdapter.SlotsViewHolder>() {

    var activity: Activity
    var context: Context
    var slotList: ArrayList<String>
    var mInflater: LayoutInflater

    init {
        this.activity = activity
        this.context = context
        this.slotList = slotList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class SlotsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var tv_slot_time: TextView = itemView.findViewById(R.id.tv_slot_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotsViewHolder {
        val itemView = mInflater.inflate(R.layout.slots_row, parent, false)
        return SlotsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SlotsViewHolder, position: Int) {
        holder.tv_slot_time.text = slotList.get(position)
    }

    override fun getItemCount(): Int {
        if(slotList.size > 4)
        {
            return 4
        }
        else{
            return slotList.size
        }

    }
}
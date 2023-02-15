package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.RVTimes

class RVTimeAdapter(context: Context, rvTimeList: ArrayList<RVTimes>): RecyclerView.Adapter<RVTimeAdapter.RVTimeViewHolder>() {

    var context: Context
    var rvTimeList: ArrayList<RVTimes>
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.rvTimeList = rvTimeList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class RVTimeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val txt_time: TextView = itemView.findViewById(R.id.txt_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVTimeViewHolder {
        val itemView = mInflater.inflate(R.layout.rvtime_row, parent, false)
        return RVTimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RVTimeViewHolder, position: Int) {
        val dp_time = rvTimeList.get(position).display_time
        holder.txt_time.text = dp_time.replace("am","AM").replace("pm","PM")
    }

    override fun getItemCount(): Int {
        return rvTimeList.size
    }

}
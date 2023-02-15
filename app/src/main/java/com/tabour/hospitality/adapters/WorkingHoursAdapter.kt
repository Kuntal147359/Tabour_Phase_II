package com.tabour.hospitality.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.WorkingHours

class WorkingHoursAdapter(
    activity: Activity,
    context: Context,
    wrknhrsList: ArrayList<WorkingHours>
): RecyclerView.Adapter<WorkingHoursAdapter.WorkingHoursViewHolder>() {

    var context: Context
    var activity: Activity
    var wrknhrsList: ArrayList<WorkingHours>
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.activity = activity
        this.wrknhrsList = wrknhrsList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class WorkingHoursViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        val tv_day: TextView = itemView.findViewById(R.id.tv_day)
        val tv_wrkng_hrs: TextView = itemView.findViewById(R.id.tv_wrkng_hrs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkingHoursViewHolder {
        val itemView = mInflater.inflate(R.layout.lyt_working_hrs_row, parent, false)
        return WorkingHoursViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkingHoursViewHolder, position: Int) {
        with(holder)
        {
            this.tv_day.text = wrknhrsList.get(position).day

            if(wrknhrsList.get(position).opening_time.equals("Closed"))
            {
                this.tv_wrkng_hrs.text = "Closed"
            }
            else
            {
                val opening_time = wrknhrsList.get(position).opening_time
                    .replace("am", "AM")
                    .replace("pm", "PM")

                val closing_time = wrknhrsList.get(position).closing_time
                    .replace("am", "AM")
                    .replace("pm", "PM")

                this.tv_wrkng_hrs.text = "${opening_time} to ${closing_time}"
            }
        }
    }

    override fun getItemCount(): Int {
        return wrknhrsList.size
    }


}
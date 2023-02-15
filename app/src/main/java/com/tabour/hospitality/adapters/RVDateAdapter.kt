package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.RVDates

class RVDateAdapter(context: Context, rvDateList: ArrayList<RVDates>) : RecyclerView.Adapter<RVDateAdapter.RVDataViewHolder>() {

    var context: Context
    var rvDateList: ArrayList<RVDates>
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.rvDateList = rvDateList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class RVDataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val txt_date: TextView = itemView.findViewById(R.id.txt_date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVDataViewHolder {
        val itemView = mInflater.inflate(R.layout.rvdate_row, parent, false)
        return RVDataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RVDataViewHolder, position: Int) {
        holder.txt_date.text = rvDateList.get(position).display_date
    }

    override fun getItemCount(): Int {
        return rvDateList.size
    }

}
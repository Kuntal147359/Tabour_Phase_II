package com.tabour.hospitality.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.tabour.hospitality.R
import com.tabour.hospitality.models.RVDates
import com.tabour.hospitality.models.ResvtnChsDt
import com.tabour.hospitality.utils.ItemClickListener

class ResvtnChsDtAdapter(context: Context, rvDateList: ArrayList<ResvtnChsDt>, selectedDate: String): RecyclerView.Adapter<ResvtnChsDtAdapter.ResvtnChsDtViewHolder>() {

    var context: Context
    var rvDateList: ArrayList<ResvtnChsDt>
    var selectedDate: String
    var mInflater: LayoutInflater
    lateinit var clickListener: ItemClickListener
    var selectedPosition = -1
    var defaultSelection = 0

    init {
        this.context = context
        this.rvDateList = rvDateList
        this.selectedDate = selectedDate
        this.mInflater = LayoutInflater.from(context)
    }

    inner class ResvtnChsDtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lyt_parent:LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var txt_day: TextView = itemView.findViewById(R.id.txt_day)
        var txt_date: TextView = itemView.findViewById(R.id.txt_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResvtnChsDtViewHolder {
        val itemView = mInflater.inflate(R.layout.rsrvtn_chs_dt_row, parent, false)
        return ResvtnChsDtViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ResvtnChsDtViewHolder, position: Int) {

        if(defaultSelection == 0)
        {
            val comp_a = "${rvDateList.get(position).display_day}, ${rvDateList.get(position).display_date} ${rvDateList.get(position).month}"

            if(selectedDate.contains(comp_a))
            {
                selectedPosition = position
                defaultSelection = 1

                clickListener.let {
                    clickListener.onClick(holder.lyt_parent,"choosedefaultdate", position)
                }
            }
        }

        if(selectedPosition==position)
        {
            holder.txt_date.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.txt_day.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.lyt_parent.setBackgroundResource(R.drawable.bezier_curve_filled_rectangle)
        }
        else
        {
            holder.txt_date.setTextColor(ContextCompat.getColor(context,R.color.h_text_color))
            holder.txt_day.setTextColor(ContextCompat.getColor(context,R.color.h_text_color))
            holder.lyt_parent.setBackgroundColor(ContextCompat.getColor(context,R.color.lyt_explore_bg_color))
        }

        holder.txt_date.text = rvDateList.get(position).display_date
        holder.txt_day.text = rvDateList.get(position).display_day
        holder.lyt_parent.setOnClickListener { view ->
            selectedPosition = position
            notifyDataSetChanged()
            clickListener.let {
                clickListener.onClick(view,"choosedate", position)
            }
        }
    }

    @JvmName("setClickListener1")
    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return rvDateList.size
    }


}
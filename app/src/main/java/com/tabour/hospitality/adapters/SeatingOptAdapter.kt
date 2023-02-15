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
import com.tabour.hospitality.R
import com.tabour.hospitality.models.Seating
import com.tabour.hospitality.utils.ItemClickListener

class SeatingOptAdapter(context: Context, seatingoptList: ArrayList<Seating>) :
    RecyclerView.Adapter<SeatingOptAdapter.SeatingOptViewHolder>() {

    var context: Context
    var seatingoptList: ArrayList<Seating>
    var mInflater: LayoutInflater
    lateinit var clickListener: ItemClickListener
    var selectedPosition = -1

    init {
        this.context = context
        this.seatingoptList = seatingoptList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class SeatingOptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var txt_seating_option: TextView = itemView.findViewById(R.id.txt_seating_option)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatingOptViewHolder {
        val itemView = mInflater.inflate(R.layout.seating_options_row, parent, false)
        return SeatingOptViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SeatingOptViewHolder, position: Int) {
        if(selectedPosition==position)
        {
            holder.txt_seating_option.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.lyt_parent.setBackgroundResource(R.drawable.bezier_curve_filled_rectangle)
        }
        else
        {
            holder.txt_seating_option.setTextColor(ContextCompat.getColor(context,R.color.h_text_color))
            holder.lyt_parent.setBackgroundColor(ContextCompat.getColor(context,R.color.lyt_explore_bg_color))
        }

        holder.txt_seating_option.text = seatingoptList.get(position).seating_option
        holder.lyt_parent.setOnClickListener { view ->
            selectedPosition = position
            notifyDataSetChanged()
            clickListener.let {
                clickListener.onClick(view,"seating", position)
            }
        }
    }

    @JvmName("setClickListener1")
    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }


    override fun getItemCount(): Int {
        return seatingoptList.size
    }
}
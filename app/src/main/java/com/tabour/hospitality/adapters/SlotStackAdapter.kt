package com.tabour.hospitality.adapters

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class SlotStackAdapter(context: Context, slotList: ArrayList<String>, resource: Int):
    ArrayAdapter<String>(context, resource) {

    var itemLayout: Int
    var slotList: ArrayList<String>

    init {
        this.slotList = slotList
        this.itemLayout = resource
    }

    override fun getCount(): Int {
        return slotList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        }
        val slot = slotList.get(position)
//        val textView: TextView = convertView!!.findViewById(R.id.tv_slot_time)
//        textView.text = slot
        return convertView!!
    }

}
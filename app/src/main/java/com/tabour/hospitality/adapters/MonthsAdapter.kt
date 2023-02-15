package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.Countries
import com.tabour.hospitality.utils.AppCommonUtils
import java.security.AccessController.getContext

class MonthsAdapter(
    context: Context,
    textViewResourceId: Int,
    month_list: ArrayList<String>
): ArrayAdapter<String>(context, textViewResourceId, month_list) {

    var month_list: ArrayList<String>

    init {
        this.month_list = month_list
    }

    override fun getCount(): Int {
        return month_list.size
    }

    override fun getItem(position: Int): String {
        return month_list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        var itemView = convertView

//        // of the recyclable view is null then inflate the custom layout for the same
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.lyt_month_row, parent, false);
        }

        val monthView = itemView!!.findViewById<TextView>(R.id.month_name)
        monthView.setText(
            month_list.get(position)
        )

        // And finally return your dynamic (or custom) view for each spinner item
        return itemView
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {

        var itemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.lyt_month_row, parent, false);
        }

        val monthView = itemView!!.findViewById<TextView>(R.id.month_name)
        monthView.setText(
            month_list.get(position)
        )

        return itemView

    }

}
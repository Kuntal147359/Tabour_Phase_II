package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.Countries
import com.tabour.hospitality.utils.AppCommonUtils.Companion.localeToEmoji
import java.util.*
import kotlin.collections.ArrayList

class CountryWithFlagAdapter(
    context: Context,
    textViewResourceId: Int,
    codewithflag_list: ArrayList<Countries>
) : ArrayAdapter<Countries>(context, textViewResourceId, codewithflag_list) {

    var codewithflag_list: ArrayList<Countries>
    var listFiltered: ArrayList<Countries>

    init {
        this.codewithflag_list = codewithflag_list
        this.listFiltered = codewithflag_list
    }

    override fun getCount(): Int {
        return listFiltered.size
    }

    override fun getItem(position: Int): Countries {
        return listFiltered.get(position)
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
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.country_with_flag_row, parent, false);
        }

        val flagView = itemView!!.findViewById<TextView>(R.id.country_flag)
        flagView.setText(
            localeToEmoji(listFiltered.get(position).iso2)
        )

        val cntry_name = itemView.findViewById<TextView>(R.id.country_name)
        cntry_name.setText(listFiltered.get(position).name)

        val phone_code = itemView.findViewById<TextView>(R.id.country_phn_code)
        phone_code.setText(listFiltered.get(position).phonecode)

//        val label = super.getView(position, convertView, parent!!) as TextView
//        label.setTextColor(Color.BLACK)
//        // Then you can get the current item using the values array (Users array) and the current position
//        // You can NOW reference each method you has created in your bean object (User class)
//        label.setText(
//            localeToEmoji(codewithflag_list.get(position).iso2)
//        )

        // And finally return your dynamic (or custom) view for each spinner item
        return itemView
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {
//        val label = super.getDropDownView(position, convertView, parent) as TextView
////        label.setTextColor(Color.BLACK)
//        label.setText(localeToEmoji(codewithflag_list.get(position).iso2))
//        return label

        var itemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.country_with_flag_row, parent, false);
        }

        val flagView = itemView!!.findViewById<TextView>(R.id.country_flag)
        flagView.setText(
            localeToEmoji(listFiltered.get(position).iso2)
        )

        val cntry_name = itemView.findViewById<TextView>(R.id.country_name)
        cntry_name.setText(listFiltered.get(position).name)

        val phone_code = itemView.findViewById<TextView>(R.id.country_phn_code)
        phone_code.setText(listFiltered.get(position).phonecode)

        return itemView

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listFiltered = codewithflag_list
                } else {
                    val filteredList: ArrayList<Countries> = ArrayList()
                    for (row in codewithflag_list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name.lowercase(Locale.ROOT)
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                listFiltered = filterResults.values as ArrayList<Countries>
                notifyDataSetChanged()
            }
        }
    }

}
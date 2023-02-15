package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.models.EshtablishmentDetails


class RestaurantImageSliderAdapter(
    context: Context,
    viewPager2: ViewPager2,
    sliderItems: ArrayList<String>
) : RecyclerView.Adapter<RestaurantImageSliderAdapter.SliderViewHolder>() {

    var context: Context
    var sliderItems: ArrayList<String>
    var viewPager2: ViewPager2

    init {
        this.context = context
        this.sliderItems = sliderItems
        this.viewPager2 = viewPager2
    }

    inner class SliderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var res_image: AppCompatImageView = itemView.findViewById(R.id.res_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.restaurantslider_row, parent, false)
        return SliderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        // For restaurant images
        Glide.with(context)
            .load(sliderItems.get(position).toString())
            .into(holder.res_image)
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

}
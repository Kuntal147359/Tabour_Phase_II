package com.tabour.hospitality.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.Categories
import com.tabour.hospitality.viewmodels.HomeViewModel


class CategoryAdapter(activity: Activity, context: Context,homeViewModel: HomeViewModel, categoriesList: ArrayList<Categories>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var context: Context
    lateinit var onListItemClick: OnListItemClick
    var activity: Activity
    var homeViewModel: HomeViewModel
    var categoriesList: ArrayList<Categories>
    var mInflater: LayoutInflater

    init {
        this.activity = activity
        this.context = context
        this.homeViewModel = homeViewModel
        this.categoriesList = categoriesList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var imageViews_cat_img: CircularImageView = itemView.findViewById(R.id.imageViews_cat_img)
        var tv_catname: TextView = itemView.findViewById(R.id.tv_catname)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = mInflater.inflate(R.layout.categories_row, parent, false)
        return CategoryViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // For category image
        Glide.with(context)
            .load(categoriesList.get(position).image)
            .error(R.drawable.cat_img)
            .into(holder.imageViews_cat_img)

        holder.tv_catname.text = categoriesList.get(position).name
        holder.lyt_parent.setOnClickListener {
            onListItemClick.onClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    fun setClickListener(context: OnListItemClick) {
        this.onListItemClick = context
    }


    interface OnListItemClick {
        fun onClick(view: View?, position: Int)
    }

}
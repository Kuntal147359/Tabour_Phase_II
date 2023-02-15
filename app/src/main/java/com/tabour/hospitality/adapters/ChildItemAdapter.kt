package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.models.ChildItem

class ChildItemAdapter(context: Context, ChildItemList: ArrayList<ChildItem>): RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder>() {

    private var context: Context
    private var ChildItemList: ArrayList<ChildItem>

    init {
        this.context = context
        this.ChildItemList = ChildItemList
    }

    inner class ChildViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_menuitem_name: TextView = itemView.findViewById(R.id.tv_menuitem_name)
        val tv_menuitem_details: TextView = itemView.findViewById(R.id.tv_menuitem_details)
        val tv_menuitem_price: TextView = itemView.findViewById(R.id.tv_menuitem_price)
        val img_menuitem_image: ImageView = itemView.findViewById(R.id.img_menuitem_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.child_item, parent, false)
        return ChildViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val childItem = ChildItemList.get(position)

        holder.let {
            // For restaurant menu images
            Glide.with(context)
                .load(ChildItemList.get(position).item_image)
                .into(it.img_menuitem_image)

            it.tv_menuitem_name.text = childItem.item_name
            it.tv_menuitem_details.text = childItem.details
            it.tv_menuitem_price.text = "QAR ${childItem.price}"
        }
    }

    override fun getItemCount(): Int {
        return ChildItemList.size
    }


}
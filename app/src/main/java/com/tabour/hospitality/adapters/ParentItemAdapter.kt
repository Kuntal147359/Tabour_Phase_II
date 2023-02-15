package com.tabour.hospitality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tabour.hospitality.R
import com.tabour.hospitality.models.ParentItem

class ParentItemAdapter(context: Context, itemList: ArrayList<ParentItem>): RecyclerView.Adapter<ParentItemAdapter.ParentViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var context: Context
    private var itemList: ArrayList<ParentItem>

    init {
        this.context = context
        this.itemList = itemList
    }

    inner class ParentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
        var ParentItemTitle: TextView = itemView.findViewById(R.id.parent_item_title)
        var ChildRecyclerView: RecyclerView = itemView.findViewById(R.id.child_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val itemView = LayoutInflater.from(parent.getContext())
            .inflate(
                R.layout.parent_item,
                parent, false)
        return ParentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem: ParentItem = itemList.get(position)
        holder.ParentItemTitle.text = parentItem.ParentItemTitle

        val layoutManager = LinearLayoutManager(
            holder.ChildRecyclerView
                .getContext()
        )

        layoutManager
            .setInitialPrefetchItemCount(
                parentItem
                    .ChildItemList
                    .size
            )

        val childItemAdapter = ChildItemAdapter(
            context,
            parentItem.ChildItemList
        )

        holder
            .ChildRecyclerView
            .setLayoutManager(layoutManager)

        holder
            .ChildRecyclerView
            .setAdapter(childItemAdapter)

        holder
            .ChildRecyclerView
            .setRecycledViewPool(viewPool)

        holder.lyt_parent.setOnClickListener {

            if(holder.ChildRecyclerView.visibility == View.GONE)
            {
                holder.ChildRecyclerView.visibility = View.VISIBLE
            }
            else
            {
                holder.ChildRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
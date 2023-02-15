package com.tabour.hospitality.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tabour.hospitality.R
import com.tabour.hospitality.fragments.ChooseseatingQueueFragment
import com.tabour.hospitality.fragments.ResdetailsFragment
import com.tabour.hospitality.fragments.ReservationFragment
import com.tabour.hospitality.models.ExploreRes
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.ItemClickListener
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.HomeViewModel

class ExploreResAdapter(activity: Activity, context: Context, explrResList: ArrayList<ExploreRes>) :
    RecyclerView.Adapter<ExploreResAdapter.ExploreResViewHolder>() {

    var context: Context
    var activity: Activity
    var explrResList: ArrayList<ExploreRes>
    lateinit var clickListener: ItemClickListener
    var mInflater: LayoutInflater

    init {
        this.context = context
        this.activity = activity
        this.explrResList = explrResList
        this.mInflater = LayoutInflater.from(context)
    }

    inner class ExploreResViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lyt_parent: LinearLayoutCompat = itemView.findViewById(R.id.lyt_parent)
        var notification_bell: LinearLayout = itemView.findViewById(R.id.notification_bell)
        var img_save: ImageView = itemView.findViewById(R.id.img_save)
        var lyt_book_now: LinearLayout = itemView.findViewById(R.id.lyt_book_now)
        var lyt_join_queue: LinearLayout = itemView.findViewById(R.id.lyt_join_queue)
        var res_image: AppCompatImageView = itemView.findViewById(R.id.res_image)
        var tv_rest_name: TextView = itemView.findViewById(R.id.tv_rest_name)
        var tv_rest_address: TextView = itemView.findViewById(R.id.tv_rest_address)
        var tv_cuisine_type: TextView = itemView.findViewById(R.id.tv_cuisine_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreResViewHolder {
        val itemView = mInflater.inflate(R.layout.explore_row, parent, false)
        return ExploreResViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ExploreResViewHolder, position: Int) {
        Glide.with(context)
            .load(explrResList.get(position).image)
            .error(R.drawable.cat_img)
            .into(holder.res_image)

        holder.apply {
            this.res_image.clipToOutline = true
            this.tv_rest_name.text = explrResList.get(position).name
            this.tv_rest_address.text = explrResList.get(position).address
            this.tv_cuisine_type.text = explrResList.get(position).cuisine_type

            if(explrResList.get(position).book_now_btn.equals("0"))
            {
                this.lyt_book_now.visibility = View.GONE
            }
            else
            {
                this.lyt_book_now.visibility = View.VISIBLE
            }

            if(explrResList.get(position).queue_btn.equals("0"))
            {
                this.lyt_join_queue.visibility = View.GONE
            }
            else
            {
                this.lyt_join_queue.visibility = View.VISIBLE
            }

            if(explrResList.get(position).is_saved.equals("0"))
            {
                this.img_save.setImageResource(R.drawable.unsave_icon)
            }
            else
            {
                this.img_save.setImageResource(R.drawable.save_icon)
            }

            this.lyt_parent.setOnClickListener {view ->
                AppCommonUtils.hideKeyboard(context)

                clickListener.let {
                    clickListener.onClick(view,"Exploreresdtls",position)
                }

//                val bundle = Bundle()
//                bundle.putString("res_id",explrResList.get(position).id.toString())
//                val resdetailsFragment = ResdetailsFragment()
//                resdetailsFragment.arguments = bundle
//                AppCommonUtils.loadFragment(activity, resdetailsFragment)
            }

//            this.notification_bell.setOnClickListener { view ->
//
//                clickListener.let {
//                    clickListener.onClick(view,"Explore",position)
//                }
//            }

            this.lyt_book_now.setOnClickListener {view ->
                AppCommonUtils.hideKeyboard(activity)

                clickListener.let {
                    clickListener.onClick(view,"Book_now_explore",position)
                }
            }

            this.lyt_join_queue.setOnClickListener{ view ->

                clickListener.let {
                    clickListener.onClick(view,"checkqueueexplore", position) // call the onClick in the OnItemClickListener
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return explrResList.size
    }

    @JvmName("setClickListener1")
    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }
}
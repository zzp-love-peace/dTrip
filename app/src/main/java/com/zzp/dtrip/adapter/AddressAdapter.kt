package com.zzp.dtrip.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.NearbyActivity
import com.zzp.dtrip.data.BigData
import com.zzp.dtrip.fragment.TripFragment

class  AddressAdapter(private val activity: Activity, private val resultList: List<BigData>) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleText: TextView = view.findViewById(R.id.title_text)
        val addressText: TextView = view.findViewById(R.id.address_text)
        val distanceText: TextView = view.findViewById(R.id.distance_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.address_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            TripFragment.position = viewHolder.adapterPosition
            activity.onBackPressed()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = resultList[position]
        holder.titleText.text = address.title
        holder.addressText.text = address.address
        if (activity.toString().contains("NearbyActivity")) {
            holder.distanceText.visibility = View.VISIBLE
            if (address._distance > 1000) {
                holder.distanceText.text = String.format("%.1f", address._distance / 1000) + "千米"
            } else {
                holder.distanceText.text = address._distance.toInt().toString() + "米"
            }
        }
    }

    override fun getItemCount() = resultList.size
}
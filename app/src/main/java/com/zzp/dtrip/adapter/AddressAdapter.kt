package com.zzp.dtrip.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.data.Data
import com.zzp.dtrip.fragment.TripFragment

class AddressAdapter(private val activity: Activity, private val resultList: List<Data>) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleText: TextView = view.findViewById(R.id.title_text)
        val addressText: TextView = view.findViewById(R.id.address_text)
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
    }

    override fun getItemCount() = resultList.size
}
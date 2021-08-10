package com.zzp.dtrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.util.SearchAPIHelper

class SearchDataAdapter(private val searchData: SearchAPIHelper.Response<List<SearchAPIHelper.NearbySearchData>>) :
    RecyclerView.Adapter<SearchDataAdapter.ViewHolder>() {

    private val data = searchData.data

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_text)
        val address: TextView = view.findViewById(R.id.address_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.address_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = data[position]

    }

    override fun getItemCount() = data.size
}
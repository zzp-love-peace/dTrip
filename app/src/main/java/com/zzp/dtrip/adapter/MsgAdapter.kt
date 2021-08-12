package com.zzp.dtrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R

class MsgAdapter ( private val msgList: List<String>) :
    RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgText: TextView = view.findViewById(R.id.left_msg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = msgList[position]
        holder.msgText.text = msg
    }

    override fun getItemCount() = msgList.size

}
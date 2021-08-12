package com.zzp.dtrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.lbssearch.`object`.result.*
import com.zzp.dtrip.R
import com.zzp.dtrip.util.MetricUtil

class RouteAdapter(private var result: RoutePlanningObject, private var type: RouteType, private val context: Context): RecyclerView.Adapter<RouteAdapter.RouteHolder>() {

    inner class RouteHolder(item: View): RecyclerView.ViewHolder(item) {
        val durationText = item.findViewById<TextView>(R.id.route_duration)
        val distanceText = item.findViewById<TextView>(R.id.route_distance)
    }

    enum class RouteType {Drive, Walk, Transit, Bicycle}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder = RouteHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        when (type) {
            RouteType.Drive ->
                (result as DrivingResultObject).result.routes.let {
                    holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration.toLong(), context)
                    holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                }
            RouteType.Walk ->
                (result as WalkingResultObject).result.routes.let {
                    holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration.toLong(), context)
                    holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                }
            RouteType.Transit ->
                (result as TransitResultObject).result.routes.let {
                    holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration, context)
                    holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                }
            RouteType.Bicycle ->
                (result as BicyclingResultObject).result.routes.let {
                    holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration.toLong(), context)
                    holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                }
        }
    }

    override fun getItemCount(): Int = when (type) {
        RouteType.Drive ->
            (result as DrivingResultObject).result.routes.size
        RouteType.Walk ->
            (result as WalkingResultObject).result.routes.size
        RouteType.Transit ->
            (result as TransitResultObject).result.routes.size
        RouteType.Bicycle ->
            (result as BicyclingResultObject).result.routes.size
    }

    fun setData (result: RoutePlanningObject, type: RouteType) {
        this@RouteAdapter.result = result
        this@RouteAdapter.type = type
        notifyDataSetChanged()
    }
}
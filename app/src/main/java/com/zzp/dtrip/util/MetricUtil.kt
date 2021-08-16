package com.zzp.dtrip.util

import android.content.Context
import android.util.Log
import c.t.m.g.hc
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.zzp.dtrip.R
import java.util.concurrent.TimeUnit

object MetricUtil {
    fun convertMinutesToTimeString(timeInMinutes: Long, context: Context): String {
        Log.d("DURATION", timeInMinutes.toString())
        return if (timeInMinutes < 60)
            context.getString(R.string.duration_minutes, timeInMinutes)
        else if (timeInMinutes % 60 == 0L)
            context.getString(R.string.duration_hours, TimeUnit.MINUTES.toHours(timeInMinutes))
        else
            context.getString(R.string.duration_hours_with_minutes, TimeUnit.MINUTES.toHours(timeInMinutes), timeInMinutes % 60)
    }

    fun convertMetersToDistanceString(distanceInMinutes: Float, context: Context): String {
        return if (distanceInMinutes < 1500)
            context.getString(R.string.distance_meters, distanceInMinutes.toInt())
        else
            context.getString(R.string.distance_kilometers, (distanceInMinutes / 1000).toInt())
    }

    // "借鉴"于不提供 LatLng 算距离功能的腾讯地图
    fun distanceBetween(locationOne: LatLng, locationTwo: LatLng): Double {
        val var8 = 3.141592653589793 * locationOne.latitude / 180.0
        val var10 = 3.141592653589793 * locationTwo.latitude / 180.0
        val var12 = var8 - var10
        val var14 = 3.141592653589793 * locationOne.longitude / 180.0 - 3.141592653589793 * locationTwo.longitude / 180.0
        var var16 = 2.0 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin(var12 / 2.0), 2.0) + Math.cos(var8) * Math.cos(var10) * Math.pow(
                    Math.sin(var14 / 2.0), 2.0
                )
            )
        )
        var16 *= 6378.137
        var16 = Math.round(var16 * 10000.0).toDouble() / 10000.0

        return var16 * 1000.0
    }
}
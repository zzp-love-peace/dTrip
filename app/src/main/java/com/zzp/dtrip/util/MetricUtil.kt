package com.zzp.dtrip.util

import android.content.Context
import android.util.Log
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
}
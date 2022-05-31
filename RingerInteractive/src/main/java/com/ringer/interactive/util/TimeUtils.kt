package com.ringer.interactive.util

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

val currentDate: Date
    get() = Calendar.getInstance().time

fun getElapsedTimeString(total_seconds: Long): String {
    val seconds = total_seconds % 60
    val minutes = (total_seconds / 60).toInt()
    val hours = (total_seconds / 3600).toInt()
    return "${if (hours != 0) "$hours hrs " else ""}$minutes mins $seconds sec"
}

fun Context.getHoursString(date: Date) =
    SimpleDateFormat(if (DateFormat.is24HourFormat(this)) "HH:mm" else "hh:mm aa", Locale.US)
        .format(date)
        .toString()

fun getRelativeDateString(date: Date?): String {
    val now = currentDate.time
    val time = date?.time ?: currentDate.time
    return when {
        DateUtils.isToday(time - DateUtils.DAY_IN_MILLIS) -> "Tomorrow"
        DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
        else -> DateUtils.getRelativeTimeSpanString(time, now, DateUtils.DAY_IN_MILLIS).toString()
    }
}

fun getTimeAgo(time: Long): String {
    val now = currentDate.time // get current time
    val diff = now - time // get the time difference between now and the given time

    if (diff < 0) {
        return "In the future" // if time is in the future
    }

    // return a string according to time difference from now
    return when {
        diff < MINUTE_MILLIS -> "Moments ago"
        diff < 2 * MINUTE_MILLIS -> "A minute ago"
        diff < HOUR_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 2 * HOUR_MILLIS -> "An hour ago"
        diff < DAY_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 2 * DAY_MILLIS -> "Yesterday"
        else -> {
            DateFormatSymbols().shortMonths[DateFormat.format("MM", time).toString()
                .toInt() - 1].toString() +
                    DateFormat.format(" dd, hh:mm", time).toString()
        }
    }
}
package com.ringer.interactive.service

import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

import android.os.Build

import android.app.*
import android.content.Context





class MyForegroundService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       *//* val notificationIntent = Intent(this, CallActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Calling")
            .setContentText("")
            .setSmallIcon(com.ringer.interactive.R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(this, "CHANNEL_ID")
        }
        startForeground(1, notification)
        return START_NOT_STICKY*//*
    }*/

}
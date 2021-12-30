package com.ringer.interactive.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ringer.interactive.pref.Preferences
import com.ringer.interactive.R
import com.ringer.interactive.activity.RingerScreen
import java.io.UnsupportedEncodingException

open class LibrarySDKMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e("From : ", "" + remoteMessage.from)
        Log.e("Message : ", remoteMessage.data.toString())

        Log.e("NotificationTitle : ", "" + remoteMessage.notification!!.title)

        try {
            sendNotification(this,remoteMessage)
        } catch (e: Exception) {
            Log.e("errorLib",""+e.message)
            e.printStackTrace()

        }
    }

    @Throws(UnsupportedEncodingException::class)
    open fun sendNotification(context: Context,remoteMessage: RemoteMessage) {
        var defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder
        val notificationManager: NotificationManager
        val title: String?
        val message: String?
        message = remoteMessage.data["body"]
        title = remoteMessage.data["title"]

        val notificationTitle = remoteMessage.notification!!.title
        val notificationMessage = remoteMessage.notification!!.body
        val imageUrl = remoteMessage.notification!!.imageUrl
        Log.e("imageUrl",""+imageUrl.toString())

        if (remoteMessage.notification!!.imageUrl == null){
            Preferences().setImageUrl(context, Uri.parse("https://www.fedex.com/content/dam/fedex/us-united-states/shipping/images/2020/Q3/icon_delivery_purple_lg_2143296207.png"))
        }else{
            Preferences().setImageUrl(context, remoteMessage.notification!!.imageUrl)
        }


        val intent = Intent(context, RingerScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("name", notificationTitle)
        intent.putExtra("number", notificationMessage)
        context.startActivity(intent)

        val contentIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val `when` = System.currentTimeMillis()
        val iconL = notificationIcon
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder =
            NotificationCompat.Builder(
                context,
                context.resources.getString(R.string.appbar_scrolling_view_behavior)
            ).setSmallIcon(iconL)
                .setContentTitle(title).setContentText(message).setAutoCancel(true)
                .setWhen(`when`).setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(context, R.color.purple_200)).setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.mipmap.ic_launcher
                    )
                ).setContentIntent(contentIntent).setStyle(
                    NotificationCompat.BigTextStyle().bigText(message)
                ) as NotificationCompat.Builder
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var mChannel: NotificationChannel? = null
            val importance = NotificationManager.IMPORTANCE_HIGH
            val id = context.resources.getString(R.string.default_notification_channel_id)
            val name: CharSequence = context.resources.getString(R.string.app_name)
            mChannel = NotificationChannel(id, name, importance)
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            val audioAttributes =
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build()
            mChannel.setSound(defaultSoundUri, audioAttributes)
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(mChannel)
            notificationBuilder.setChannelId(context.resources.getString(R.string.default_notification_channel_id))
        }
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        notificationManager.notify(
            System.currentTimeMillis().toInt() /* ID of notification */,
            notificationBuilder.build()
        )

    }


    private val notificationIcon: Int
        private get() {
            val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
        }
}
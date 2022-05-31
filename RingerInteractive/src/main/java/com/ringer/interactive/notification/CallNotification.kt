package com.ringer.interactive.notification

import android.app.Notification
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.telecom.Call.Details.CAPABILITY_MUTE
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ringer.interactive.R
import com.ringer.interactive.interactor.callaudio.CallAudiosInteractor
import com.ringer.interactive.interactor.calls.CallsInteractor
import com.ringer.interactive.interactor.color.ColorsInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.interactor.string.StringsInteractor
import com.ringer.interactive.model.Call
import com.ringer.interactive.receiver.CallBroadcastReceiver
import com.ringer.interactive.receiver.CallBroadcastReceiver.Companion.ACTION_HANGUP
import com.ringer.interactive.receiver.CallBroadcastReceiver.Companion.ACTION_MUTE
import com.ringer.interactive.receiver.CallBroadcastReceiver.Companion.ACTION_SPEAKER
import com.ringer.interactive.receiver.CallBroadcastReceiver.Companion.ACTION_UNMUTE
import com.ringer.interactive.receiver.CallBroadcastReceiver.Companion.ACTION_UNSPEAKER
import com.ringer.interactive.ui.call.CallActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class CallNotification @Inject constructor(
    private val calls: CallsInteractor,
    private val colors: ColorsInteractor,
    private val phones: PhonesInteractor,
    private val strings: StringsInteractor,
    private val callAudios: CallAudiosInteractor,
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat
) : CallsInteractor.Listener, CallAudiosInteractor.Listener {

    private var _call: Call? = null


    override fun onNoCalls() {
        detach()
        cancel()
    }

    override fun onCallChanged(call: Call) {
    }

    override fun onMainCallChanged(call: Call) {
        show(call)
        _call = call
    }


    override fun onMuteChanged(isMuted: Boolean) {
        _call?.let(::show)
    }

    override fun onAudioRouteChanged(audiosRoute: CallAudiosInteractor.AudioRoute) {
        _call?.let(::show)
    }


    fun attach() {
        createNotificationChannel()
        calls.registerListener(this)
        callAudios.registerListener(this)
        Thread.currentThread().setUncaughtExceptionHandler { _, _ -> cancel() }
    }

    fun detach() {
        calls.unregisterListener(this)
        callAudios.unregisterListener(this)
        cancel()
    }

    private val _contentPendingIntent by lazy {
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, CallActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            },
            FLAG_IMMUTABLE
        )
    }

    private val _answerAction by lazy {
        NotificationCompat.Action(
            R.drawable.round_call_24,
            strings.getString(R.string.action_answer),
            getCallPendingIntent(CallBroadcastReceiver.ACTION_ANSWER, 0)
        )
    }

    private val _hangupAction by lazy {
        NotificationCompat.Action(
            R.drawable.round_call_end_24,
            strings.getString(R.string.action_hangup),
            getCallPendingIntent(ACTION_HANGUP, 1)
        )
    }

    private val _muteAction by lazy {
        NotificationCompat.Action(
            R.drawable.round_mic_24,
            strings.getString(R.string.call_action_mute),
            getCallPendingIntent(ACTION_MUTE, 2)
        )
    }

    private val _unmuteAction by lazy {
        NotificationCompat.Action(
            R.drawable.img_unmute_green,
            strings.getString(R.string.call_action_unmute),
            getCallPendingIntent(ACTION_UNMUTE, 3)
        )
    }

    private val _speakerAction by lazy {
        NotificationCompat.Action(
            R.drawable.round_volume_down_24,
            strings.getString(R.string.call_action_speaker),
            getCallPendingIntent(ACTION_SPEAKER, 4)
        )
    }

    private val _unspeakerAction by lazy {
        NotificationCompat.Action(
            R.drawable.img_speaker_on_green,
            strings.getString(R.string.call_action_speaker_off),
            getCallPendingIntent(ACTION_UNSPEAKER, 5)
        )
    }

    private val _channel by lazy {
        NotificationChannelCompat.Builder(CHANNEL_ID, IMPORTANCE_HIGH)
            .setName(strings.getString(R.string.call_notification_channel_name))
            .setDescription(strings.getString(R.string.call_notification_channel_description))
            .setLightsEnabled(true)
            .build()
    }


    private fun getCallIntent(callAction: String) =
        Intent(context, CallBroadcastReceiver::class.java).apply {
            action = callAction
            putExtra(EXTRA_NOTIFICATION_ID, ID)
        }

    private fun getCallPendingIntent(callAction: String, rc: Int) =
        PendingIntent.getBroadcast(
            context,
            rc,
            getCallIntent(callAction),
            FLAG_CANCEL_CURRENT or FLAG_IMMUTABLE
        )


    private fun buildNotification(call: Call, callback: (Notification) -> Unit) {
        phones.lookupAccount(call.number) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setWhen(0)
                .setOngoing(true)
                .setColorized(true)
                .setPriority(PRIORITY)
                .setOnlyAlertOnce(true)
                .setContentTitle(it?.displayString ?: call.number)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(_contentPendingIntent)
                .setColor(colors.getColor(R.color.gray))
                .setContentText(strings.getString(call.state.stringRes))
            if (call.isIncoming) {
                builder.addAction(_answerAction)
            }
            if (call.state !in arrayOf(Call.State.DISCONNECTED, Call.State.DISCONNECTING)) {
                builder.addAction(_hangupAction)
            }
            callAudios.isMuted?.let { isMuted ->
                if (call.isCapable(CAPABILITY_MUTE)) {
                    builder.addAction(if (isMuted) _unmuteAction else _muteAction)
                }
            }
            callAudios.isSpeakerOn?.let { isSpeakerOn ->
                builder.addAction(if (isSpeakerOn) _unspeakerAction else _speakerAction)
            }
            callback.invoke(builder.build())
        }
    }


    fun show(call: Call) {
        buildNotification(call) {
            notificationManager.notify(ID, it)
        }
    }

    fun cancel() {
        notificationManager.cancel(ID)
    }

    fun createNotificationChannel() {
        notificationManager.createNotificationChannel(_channel)
    }

    companion object {
        const val ID = 420
        const val CHANNEL_ID = "call_notification_channel"
        const val PRIORITY = NotificationCompat.PRIORITY_LOW
    }
}
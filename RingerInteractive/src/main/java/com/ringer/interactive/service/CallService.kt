package com.ringer.interactive.service

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.telecom.CallAudioState
import android.telecom.InCallService
import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.interactor.callaudio.CallAudiosInteractor
import com.ringer.interactive.interactor.calls.CallsInteractor
import com.ringer.interactive.model.Call
import com.ringer.interactive.notification.CallNotification
import com.ringer.interactive.repository.calls.CallsRepository
import com.ringer.interactive.ui.call.CallActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("NewApi")
@AndroidEntryPoint
class CallService : InCallService() {
    @Inject lateinit var callAudios: CallAudiosInteractor
    @Inject lateinit var callsRepository: CallsRepository
    @Inject lateinit var callsInteractor: CallsInteractor
    @Inject lateinit var callNotification: CallNotification

    val calls = MutableLiveData<List<Call>>()


    override fun onCreate() {
        super.onCreate()
        sInstance = this
        callNotification.attach()
    }

    override fun onDestroy() {
        callNotification.detach()
        callNotification.cancel()
        super.onDestroy()
    }

    override fun onCallAdded(telecomCall: android.telecom.Call) {
        super.onCallAdded(telecomCall)
        addCall(Call(telecomCall))
        callsInteractor.entryAddCall(Call(telecomCall))
        if (!sIsActivityActive) {
            startCallActivity()
        }
    }

    override fun onCallRemoved(telecomCall: android.telecom.Call) {
        super.onCallRemoved(telecomCall)
        removeCall(Call(telecomCall))
        callsInteractor.getCallByTelecomCall(telecomCall)
            ?.let(callsInteractor::entryRemoveCall)
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        super.onCallAudioStateChanged(audioState)
        callAudios.entryCallAudioStateChanged(callAudioState)
    }

    private fun startCallActivity() {
        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun addCall(call: Call) {
        val list = calls.value?.toMutableList()
        list?.add(call)
        calls.value = list
    }

    private fun removeCall(call: Call) {
        val list = calls.value?.toMutableList()
        list?.remove(call)
        calls.value = list
    }


    companion object {
        var sIsActivityActive = false
        var sInstance: CallService? = null
    }
}
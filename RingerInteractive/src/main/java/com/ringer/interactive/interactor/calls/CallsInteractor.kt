package com.ringer.interactive.interactor.calls

import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.model.Call

interface CallsInteractor : BaseInteractor<CallsInteractor.Listener>, Call.Listener {
    interface Listener {
        fun onNoCalls()
        fun onCallChanged(call: Call)
        fun onMainCallChanged(call: Call)
    }

    val mainCall: Call?
    val callsCount: Int
    val isMultiCall: Boolean

    fun getStateCount(state: Call.State): Int
    fun getFirstState(state: Call.State): Call?
    fun getCallByTelecomCall(telecomCall: android.telecom.Call): Call?

    fun swapCall(callId: String)
    fun holdCall(callId: String)
    fun mergeCall(callId: String)
    fun unHoldCall(callId: String)
    fun toggleHold(callId: String)
    fun answerCall(callId: String)
    fun rejectCall(callId: String)
    fun invokeCallKey(callId: String, c: Char)


    fun entryAddCall(call: Call)
    fun entryRemoveCall(call: Call)
}

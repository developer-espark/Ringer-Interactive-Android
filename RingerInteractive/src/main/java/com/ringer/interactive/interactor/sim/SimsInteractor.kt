package com.ringer.interactive.interactor.sim

import android.telecom.PhoneAccount
import android.telephony.SubscriptionInfo
import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.model.SimAccount

interface SimsInteractor : BaseInteractor<SimsInteractor.Listener> {
    interface Listener

    fun getIsMultiSim(callback: (Boolean) -> Unit)
    fun getSimAccounts(callback: (List<SimAccount>) -> Unit)
    fun getPhoneAccounts(callback: (List<PhoneAccount>) -> Unit)
    fun getSubscriptionInfos(callback: (List<SubscriptionInfo>) -> Unit)
}
package com.ringer.interactive.model

import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import com.ringer.interactive.util.fullAddress
import com.ringer.interactive.util.fullLabel

data class SimAccount(
    val index: Int,
    val phoneAccount: PhoneAccount
) {
    val label: String
    val address: String
    val phoneAccountHandle: PhoneAccountHandle get() = phoneAccount.accountHandle

    init {
        label = phoneAccount.fullLabel()
        address = phoneAccount.fullAddress()
    }
}
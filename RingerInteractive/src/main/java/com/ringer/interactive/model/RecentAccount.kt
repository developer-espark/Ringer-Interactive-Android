package com.ringer.interactive.model

import android.provider.CallLog
import androidx.annotation.IntDef
import com.ringer.interactive.util.getTimeAgo
import java.io.Serializable
import java.util.*

data class RecentAccount(
    val number: String,
    @CallType val type: Int,
    val id: Long = 0,
    val duration: Long = 0,
    val date: Date? = null,
    val cachedName: String? = null,
) : Serializable {

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        TYPE_INCOMING,
        TYPE_OUTGOING,
        TYPE_MISSED,
        TYPE_BLOCKED,
        TYPE_VOICEMAIL,
        TYPE_REJECTED,
        TYPE_UNKNOWN
    )
    annotation class CallType

    val relativeTime: String?
        get() = date?.time?.let { getTimeAgo(it) }

    companion object {
        const val TYPE_UNKNOWN = 7
        const val TYPE_MISSED = CallLog.Calls.MISSED_TYPE
        const val TYPE_BLOCKED = CallLog.Calls.BLOCKED_TYPE
        const val TYPE_INCOMING = CallLog.Calls.INCOMING_TYPE
        const val TYPE_OUTGOING = CallLog.Calls.OUTGOING_TYPE
        const val TYPE_REJECTED = CallLog.Calls.REJECTED_TYPE
        const val TYPE_VOICEMAIL = CallLog.Calls.VOICEMAIL_TYPE
    }
}
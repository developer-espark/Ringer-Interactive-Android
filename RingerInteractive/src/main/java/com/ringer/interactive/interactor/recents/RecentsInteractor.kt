package com.ringer.interactive.interactor.recents

import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.model.RecentAccount

interface RecentsInteractor : BaseInteractor<RecentsInteractor.Listener> {
    interface Listener

    fun deleteRecent(recentId: Long)
    fun queryRecent(recentId: Long): RecentAccount?
    fun queryRecent(recentId: Long, callback: (RecentAccount?) -> Unit)
    fun getCallTypeImage(@RecentAccount.CallType callType: Int): Int
    fun getLastOutgoingCall(): String
}
package com.ringer.interactive.interactor.blocked

import com.ringer.interactive.interactor.base.BaseInteractor

interface BlockedInteractor : BaseInteractor<BlockedInteractor.Listener> {
    interface Listener

    fun blockNumber(number: String)
    fun unblockNumber(number: String)
    fun isNumberBlocked(number: String): Boolean
}
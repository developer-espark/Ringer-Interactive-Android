package com.ringer.interactive.interactor.call

import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.model.SimAccount

interface CallNavigationsInteractor : BaseInteractor<CallNavigationsInteractor.Listener> {
    interface Listener

    fun callVoicemail()
    fun call(number: String)
    fun call(simAccount: SimAccount?, number: String)
}
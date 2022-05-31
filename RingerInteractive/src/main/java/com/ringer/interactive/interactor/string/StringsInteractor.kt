package com.ringer.interactive.interactor.string

import androidx.annotation.StringRes
import com.ringer.interactive.interactor.base.BaseInteractor

interface StringsInteractor : BaseInteractor<StringsInteractor.Listener> {
    interface Listener

    fun getString(@StringRes stringRes: Int): String
}
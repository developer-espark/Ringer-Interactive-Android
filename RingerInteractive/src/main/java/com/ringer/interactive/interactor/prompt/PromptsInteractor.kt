package com.ringer.interactive.interactor.prompt

import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.ui.base.BaseFragment

interface PromptsInteractor : BaseInteractor<PromptsInteractor.Listener> {
    interface Listener

    fun showFragment(fragment: BaseFragment<*>, tag: String? = null)
}
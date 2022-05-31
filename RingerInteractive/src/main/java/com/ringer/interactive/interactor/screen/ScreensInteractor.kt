package com.ringer.interactive.interactor.screen

import android.view.MotionEvent
import com.ringer.interactive.interactor.base.BaseInteractor

interface ScreensInteractor : BaseInteractor<ScreensInteractor.Listener> {
    interface Listener

    fun showWhenLocked()
    fun disableKeyboard()
    fun ignoreEditTextFocus(event: MotionEvent)
}
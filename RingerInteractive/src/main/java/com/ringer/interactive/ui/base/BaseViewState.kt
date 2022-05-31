package com.ringer.interactive.ui.base

import androidx.lifecycle.ViewModel
import com.ringer.interactive.util.DataLiveEvent
import com.ringer.interactive.util.LiveEvent

open class BaseViewState : ViewModel() {
    val finishEvent = LiveEvent()
    val errorEvent = DataLiveEvent<Int>()
    val messageEvent = DataLiveEvent<Int>()

    open fun attach() {}
    open fun detach() {}
}

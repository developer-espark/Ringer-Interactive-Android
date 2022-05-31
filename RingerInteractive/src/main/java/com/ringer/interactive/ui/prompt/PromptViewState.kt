package com.ringer.interactive.ui.prompt

import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.ui.base.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PromptViewState @Inject constructor() : BaseViewState() {
    val title = MutableLiveData<String>()
    val subtitle = MutableLiveData<String>()

    
    fun onNoClick() {
        finishEvent.call()
    }

    fun onYesClick() {
        finishEvent.call()
    }
}
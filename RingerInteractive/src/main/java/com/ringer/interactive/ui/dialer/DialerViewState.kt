package com.ringer.interactive.ui.dialer

import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.interactor.audio.AudiosInteractor
import com.ringer.interactive.interactor.navigation.NavigationsInteractor
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import com.ringer.interactive.interactor.recents.RecentsInteractor
import com.ringer.interactive.model.ContactAccount
import com.ringer.interactive.ui.dialpad.DialpadViewState
import com.ringer.interactive.util.DataLiveEvent
import com.ringer.interactive.util.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialerViewState @Inject constructor(
    audios: AudiosInteractor,
    preferences: PreferencesInteractor,
    private val recents: RecentsInteractor,
    private val navigations: NavigationsInteractor
) :
    DialpadViewState(audios, preferences) {

    val callVoicemailEvent = LiveEvent()
    val callNumberEvent = DataLiveEvent<String>()
    val isSuggestionsVisible = MutableLiveData(false)
    val isDeleteButtonVisible = MutableLiveData<Boolean>()
    val isAddContactButtonVisible = MutableLiveData<Boolean>()


    override fun onLongKeyClick(char: Char): Boolean {
        return if (char == '1') {
            callVoicemailEvent.call()
            true
        } else {
            super.onLongKeyClick(char)
        }
    }

    override fun onTextChanged(text: String) {
        isDeleteButtonVisible.value = text.isNotEmpty()
        isAddContactButtonVisible.value = text.isNotEmpty()
        if (text.isEmpty()) isSuggestionsVisible.value = false
        super.onTextChanged(text)
    }

    fun onDeleteClick() {
        text.value?.dropLast(1)?.let(this::onTextChanged)
    }

    fun onLongDeleteClick(): Boolean {
        onTextChanged("")
        text.value = ""
        return true
    }

    fun onCallClick() {
        if (text.value?.isEmpty() == true) {
            text.value = recents.getLastOutgoingCall()
        } else {
            text.value?.let(callNumberEvent::call)
            finishEvent.call()
        }
    }

    fun onAddContactClick() {
        text.value?.let(navigations::addContact)
    }

    fun onSuggestionsChanged(contacts: List<ContactAccount>) {
        isSuggestionsVisible.value = contacts.isNotEmpty() && text.value?.isNotEmpty() == true
    }
}
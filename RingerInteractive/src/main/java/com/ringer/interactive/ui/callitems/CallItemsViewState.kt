package com.ringer.interactive.ui.callitems

import androidx.lifecycle.LiveData
import com.ringer.interactive.model.Call
import com.ringer.interactive.repository.calls.CallsRepository
import com.ringer.interactive.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CallItemsViewState @Inject constructor(
    private val callsRepository: CallsRepository
) :
    ListViewState<Call>() {

    override fun onItemLeftClick(item: Call) {
        super.onItemLeftClick(item)
        item.leaveConference()
        finishEvent.call()
    }

    override fun onItemRightClick(item: Call) {
        super.onItemRightClick(item)
        item.reject()
        finishEvent.call()
    }

    override fun getItemsObservable(callback: (LiveData<List<Call>>) -> Unit) {
        callback.invoke(callsRepository.getCalls())
    }
}
package com.ringer.interactive.ui.recents

import androidx.lifecycle.LiveData
import com.ringer.interactive.R
import com.ringer.interactive.contentprovider.RecentsProviderLiveData
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.model.RecentAccount
import com.ringer.interactive.repository.recents.RecentsRepository
import com.ringer.interactive.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class RecentsViewState @Inject constructor(
    recentsRepository: RecentsRepository,
    private val permissions: PermissionsInteractor
) :
    ListViewState<RecentAccount>() {

    override val noResultsIconRes = R.drawable.round_history_24
    override val noResultsTextRes = R.string.error_no_results_recents
    override val noPermissionsTextRes = R.string.error_no_permissions_recents

    private val recentsLiveData = recentsRepository.getRecents() as RecentsProviderLiveData


    override fun onFilterChanged(filter: String?) {
        super.onFilterChanged(filter)
        recentsLiveData.filter = filter
    }

    override fun getItemsObservable(callback: (LiveData<List<RecentAccount>>) -> Unit) {
        permissions.runWithReadCallLogPermissions {
            onPermissionsChanged(it)
            if (it) callback.invoke(recentsLiveData)
        }
    }
}
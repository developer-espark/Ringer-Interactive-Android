package com.ringer.interactive.ui.contacts

import androidx.lifecycle.LiveData
import com.ringer.interactive.R
import com.ringer.interactive.contentprovider.ContactsProviderLiveData
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.model.ContactAccount
import com.ringer.interactive.repository.contacts.ContactsRepository
import com.ringer.interactive.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class ContactsViewState @Inject constructor(
    contactsRepository: ContactsRepository,
    private val permissions: PermissionsInteractor,
) :
    ListViewState<ContactAccount>() {

    override val noResultsIconRes = R.drawable.round_people_24
    override val noResultsTextRes = R.string.error_no_results_contacts
    override val noPermissionsTextRes = R.string.error_no_permissions_contacts

    private val contactsLiveData = contactsRepository.getContacts() as ContactsProviderLiveData


    override fun onFilterChanged(filter: String?) {
        super.onFilterChanged(filter)
        contactsLiveData.filter = filter
    }

    override fun getItemsObservable(callback: (LiveData<List<ContactAccount>>) -> Unit) {
        permissions.runWithReadContactsPermissions {
            onPermissionsChanged(it)
            if (it) callback.invoke(contactsLiveData)
        }
    }
}
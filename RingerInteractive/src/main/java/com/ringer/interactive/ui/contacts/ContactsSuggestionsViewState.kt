package com.ringer.interactive.ui.contacts

import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.repository.contacts.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactsSuggestionsViewState @Inject constructor(
    permissions: PermissionsInteractor,
    contactsRepository: ContactsRepository
) : ContactsViewState(contactsRepository, permissions)
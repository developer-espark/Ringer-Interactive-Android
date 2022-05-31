package com.ringer.interactive.ui.contacts

import androidx.fragment.app.activityViewModels
import com.ringer.interactive.adapter.ContactsAdapter
import com.ringer.interactive.model.ContactAccount
import com.ringer.interactive.ui.list.ListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class ContactsFragment @Inject constructor() :
    ListFragment<ContactAccount, ContactsViewState>() {

    @Inject override lateinit var adapter: ContactsAdapter
    override val viewState: ContactsViewState by activityViewModels()
}
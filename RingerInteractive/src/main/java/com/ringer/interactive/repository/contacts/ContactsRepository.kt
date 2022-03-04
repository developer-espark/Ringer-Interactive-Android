package com.ringer.interactive.repository.contacts

import androidx.lifecycle.LiveData
import com.ringer.interactive.model.ContactAccount

interface ContactsRepository {
    fun getContacts(): LiveData<List<ContactAccount>>
    fun getContact(contactId: Long, callback: (ContactAccount?) -> Unit)
}
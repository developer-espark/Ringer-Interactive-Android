package com.ringer.interactive.repository.contacts

import androidx.lifecycle.LiveData
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import com.ringer.interactive.di.factory.livedata.LiveDataFactory
import com.ringer.interactive.model.ContactAccount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory
) : ContactsRepository {
    override fun getContacts(): LiveData<List<ContactAccount>> =
        liveDataFactory.getContactsProviderLiveData()

    override fun getContact(contactId: Long, callback: (ContactAccount?) -> Unit) {
        contentResolverFactory.getContactsContentResolver(contactId).queryItems {
            callback.invoke(it.getOrNull(0))
        }
    }
}
package com.ringer.interactive.contentprovider

import android.content.Context
import com.ringer.interactive.contentresolver.ContactsContentResolver
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import com.ringer.interactive.model.ContactAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class ContactsProviderLiveData(
    @ApplicationContext context: Context,
    private val contactId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory
) : ContentProviderLiveData<ContactsContentResolver, ContactAccount>(context) {
    override val contentResolver by lazy { contentResolverFactory.getContactsContentResolver(contactId) }
}
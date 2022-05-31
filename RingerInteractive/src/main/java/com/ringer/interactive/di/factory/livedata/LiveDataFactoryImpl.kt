package com.ringer.interactive.di.factory.livedata

import android.content.Context
import com.ringer.interactive.contentprovider.ContactsProviderLiveData
import com.ringer.interactive.contentprovider.PhonesProviderLiveData
import com.ringer.interactive.contentprovider.RecentsProviderLiveData
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveDataFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolverFactory: ContentResolverFactory
) : LiveDataFactory {
    override fun getRecentsProviderLiveData(recentId: Long?) =
        RecentsProviderLiveData(context, recentId, contentResolverFactory)

    override fun getPhonesProviderLiveData(contactId: Long?) =
        PhonesProviderLiveData(context, contactId, contentResolverFactory)

    override fun getContactsProviderLiveData(contactId: Long?) =
        ContactsProviderLiveData(context, contactId, contentResolverFactory)
}
package com.ringer.interactive.di.factory.contentresolver

import android.content.Context
import com.ringer.interactive.contentresolver.ContactsContentResolver
import com.ringer.interactive.contentresolver.PhoneLookupContentResolver
import com.ringer.interactive.contentresolver.PhonesContentResolver
import com.ringer.interactive.contentresolver.RecentsContentResolver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentResolverFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ContentResolverFactory {
    override fun getPhoneLookupContentResolver(number: String?) =
        PhoneLookupContentResolver(context, number)

    override fun getRecentsContentResolver(recentId: Long?) =
        RecentsContentResolver(context, recentId)

    override fun getPhonesContentResolver(contactId: Long?) =
        PhonesContentResolver(context, contactId)

    override fun getContactsContentResolver(contactId: Long?) =
        ContactsContentResolver(context, contactId)
}
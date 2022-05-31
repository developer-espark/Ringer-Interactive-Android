package com.ringer.interactive.di.factory.contentresolver

import com.ringer.interactive.contentresolver.ContactsContentResolver
import com.ringer.interactive.contentresolver.PhoneLookupContentResolver
import com.ringer.interactive.contentresolver.PhonesContentResolver
import com.ringer.interactive.contentresolver.RecentsContentResolver

interface ContentResolverFactory {
    fun getPhonesContentResolver(contactId: Long? = null): PhonesContentResolver
    fun getRecentsContentResolver(recentId: Long? = null): RecentsContentResolver
    fun getPhoneLookupContentResolver(number: String?): PhoneLookupContentResolver
    fun getContactsContentResolver(contactId: Long? = null): ContactsContentResolver
}
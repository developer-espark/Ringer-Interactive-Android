package com.ringer.interactive.di.factory.livedata

import com.ringer.interactive.contentprovider.ContactsProviderLiveData
import com.ringer.interactive.contentprovider.PhonesProviderLiveData
import com.ringer.interactive.contentprovider.RecentsProviderLiveData


interface LiveDataFactory {
    fun getPhonesProviderLiveData(contactId: Long? = null): PhonesProviderLiveData
    fun getRecentsProviderLiveData(recentId: Long? = null): RecentsProviderLiveData
    fun getContactsProviderLiveData(contactId: Long? = null): ContactsProviderLiveData
}

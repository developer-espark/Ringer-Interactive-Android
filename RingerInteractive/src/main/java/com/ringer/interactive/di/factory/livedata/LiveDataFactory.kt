package com.ringer.interactive.di.factory.livedata

import com.chooloo.www.chooloolib.livedata.contentprovider.ContactsProviderLiveData
import com.chooloo.www.chooloolib.livedata.contentprovider.PhonesProviderLiveData
import com.chooloo.www.chooloolib.livedata.contentprovider.RecentsProviderLiveData

interface LiveDataFactory {
    fun getPhonesProviderLiveData(contactId: Long? = null): PhonesProviderLiveData
    fun getRecentsProviderLiveData(recentId: Long? = null): RecentsProviderLiveData
    fun getContactsProviderLiveData(contactId: Long? = null): ContactsProviderLiveData
}

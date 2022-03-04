package com.ringer.interactive.repository.phones

import com.ringer.interactive.di.factory.livedata.LiveDataFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhonesRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory
) : PhonesRepository {
    override fun getPhones(contactId: Long?) = liveDataFactory.getPhonesProviderLiveData(contactId)
}
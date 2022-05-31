package com.ringer.interactive.contentprovider

import android.content.Context
import com.ringer.interactive.contentresolver.PhonesContentResolver
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import com.ringer.interactive.model.PhoneAccount

class PhonesProviderLiveData(
    context: Context,
    private val contactId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory,
) : ContentProviderLiveData<PhonesContentResolver, PhoneAccount>(context) {
    override val contentResolver by lazy { contentResolverFactory.getPhonesContentResolver(contactId) }
}
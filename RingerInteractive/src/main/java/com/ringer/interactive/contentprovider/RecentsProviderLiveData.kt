package com.ringer.interactive.contentprovider

import android.content.Context
import com.ringer.interactive.contentresolver.RecentsContentResolver
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import com.ringer.interactive.model.RecentAccount

class RecentsProviderLiveData(
    context: Context,
    private val recentId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory
) : ContentProviderLiveData<RecentsContentResolver, RecentAccount>(context) {
    override val contentResolver by lazy { contentResolverFactory.getRecentsContentResolver(recentId) }
}
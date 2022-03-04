package com.ringer.interactive.repository.recents

import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import com.ringer.interactive.di.factory.livedata.LiveDataFactory
import com.ringer.interactive.model.RecentAccount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentsRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory
) : RecentsRepository {
    override fun getRecents() = liveDataFactory.getRecentsProviderLiveData()

    override fun getRecent(recentId: Long?, callback: (RecentAccount?) -> Unit) {
        contentResolverFactory.getRecentsContentResolver(recentId).queryItems {
            callback.invoke(it.getOrNull(0))
        }
    }
}
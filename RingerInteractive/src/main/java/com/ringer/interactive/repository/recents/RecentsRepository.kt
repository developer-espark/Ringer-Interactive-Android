package com.ringer.interactive.repository.recents

import androidx.lifecycle.LiveData
import com.ringer.interactive.model.RecentAccount

interface RecentsRepository {
    fun getRecents(): LiveData<List<RecentAccount>>
    fun getRecent(recentId: Long? = null, callback: (RecentAccount?) -> Unit)
}
package com.ringer.interactive.ui.recents

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.ringer.interactive.adapter.RecentsAdapter
import com.ringer.interactive.model.RecentAccount
import com.ringer.interactive.ui.list.ListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class RecentsFragment @Inject constructor() : ListFragment<RecentAccount, RecentsViewState>() {
    @Inject override lateinit var adapter: RecentsAdapter
    override val viewState: RecentsViewState by activityViewModels()


    companion object {
        fun newInstance(filter: String? = null) = RecentsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_FILTER, filter)
            }
        }
    }
}
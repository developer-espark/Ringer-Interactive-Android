package com.ringer.interactive.ui.callitems

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ringer.interactive.adapter.CallItemsAdapter
import com.ringer.interactive.model.Call
import com.ringer.interactive.ui.list.ListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallItemsFragment @Inject constructor() : ListFragment<Call, CallItemsViewState>() {
    override val viewState: CallItemsViewState by viewModels()

    @Inject override lateinit var adapter: CallItemsAdapter


    override fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyIcon.isVisible = false
            empty.emptyText.isVisible = false
            itemsScrollView.isVisible = !isShow
        }
    }
}
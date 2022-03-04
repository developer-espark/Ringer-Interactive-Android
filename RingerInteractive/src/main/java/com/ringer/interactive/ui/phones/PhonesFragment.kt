package com.ringer.interactive.ui.phones

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ringer.interactive.adapter.PhonesAdapter
import com.ringer.interactive.interactor.call.CallNavigationsInteractor
import com.ringer.interactive.model.PhoneAccount
import com.ringer.interactive.ui.briefcontact.BriefContactFragment.Companion.ARG_CONTACT_ID
import com.ringer.interactive.ui.list.ListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhonesFragment @Inject constructor() : ListFragment<PhoneAccount, PhonesViewState>() {
    override val viewState: PhonesViewState by viewModels()

    @Inject lateinit var callNavigations: CallNavigationsInteractor
    @Inject override lateinit var adapter: PhonesAdapter


    override fun onSetup() {
        viewState.contactId.value = args.getLong(ARG_CONTACT_ID)
        super.onSetup()
        viewState.callEvent.observe(this@PhonesFragment) {
            it.ifNew?.let(callNavigations::call)
        }
        binding.itemsScrollView.fastScroller.isVisible = false
    }

    companion object {
        fun newInstance(contactId: Long? = null) = PhonesFragment().apply {
            arguments = Bundle().apply {
                contactId?.let { putLong(ARG_CONTACT_ID, it) }
            }
        }
    }
}
package com.ringer.interactive.ui.briefcontact

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ringer.interactive.R
import com.ringer.interactive.di.factory.fragment.FragmentFactory
import com.ringer.interactive.interactor.call.CallNavigationsInteractor
import com.ringer.interactive.interactor.dialog.DialogsInteractor
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BriefContactFragment @Inject constructor() : BaseFragment<BriefContactViewState>() {
    override val contentView by lazy { binding.root }
    override val viewState: BriefContactViewState by viewModels()

    protected val binding by lazy { com.ringer.interactive.databinding.BriefContactBinding.inflate(layoutInflater) }
    private val phonesFragment by lazy { fragmentFactory.getPhonesFragment(viewState.contactId.value) }

    @Inject lateinit var callNavigations: CallNavigationsInteractor
    @Inject lateinit var dialogs: DialogsInteractor
    @Inject lateinit var fragmentFactory: FragmentFactory
    @Inject lateinit var permissions: PermissionsInteractor


    override fun onSetup() {
        binding.apply {
            contactButtonSms.setOnClickListener {
                viewState.onActionSms()
            }

            contactButtonCall.setOnClickListener {
                viewState.onActionCall()
            }

            contactButtonEdit.setOnClickListener {
                viewState.onActionEdit()
            }

            contactButtonDelete.setOnClickListener {
                viewState.onActionDelete()
            }

            briefContactStarButton.setOnClickListener {
                viewState.onActionStar(it.isActivated)
            }
        }

        viewState.apply {
            contactName.observe(this@BriefContactFragment) {
                binding.briefContactTextName.text = it
            }

            contactImage.observe(this@BriefContactFragment) {
                binding.briefContactImage.apply {
                    setImageURI(it)
                    isVisible = it != null
                }
            }

            isStarIconVisible.observe(this@BriefContactFragment) {
                binding.briefContactStarButton.isVisible = it
            }

            isStarIconActivated.observe(this@BriefContactFragment) {
                binding.briefContactStarButton.isActivated = it
            }

            callEvent.observe(this@BriefContactFragment) {
                it.ifNew?.let(callNavigations::call)
            }

            confirmContactDeleteEvent.observe(this@BriefContactFragment) {
                it.ifNew?.let {
                    dialogs.askForValidation(R.string.explain_delete_contact) { bl ->
                        if (bl) onDeleteConfirmed()
                    }
                }
            }

            onContactId(args.getLong(ARG_CONTACT_ID))
        }

        childFragmentManager
            .beginTransaction()
            .replace(binding.contactPhonesFragmentContainer.id, phonesFragment)
            .commitNow()
    }


    companion object {
        const val ARG_CONTACT_ID = "contact_id"

        fun newInstance(contactId: Long) = BriefContactFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_CONTACT_ID, contactId)
            }
        }
    }
}
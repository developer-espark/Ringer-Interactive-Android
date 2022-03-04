package com.ringer.interactive.ui.briefcontact

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.R
import com.ringer.interactive.interactor.contacts.ContactsInteractor
import com.ringer.interactive.interactor.navigation.NavigationsInteractor
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.model.ContactAccount
import com.ringer.interactive.model.PhoneAccount
import com.ringer.interactive.ui.base.BaseViewState
import com.ringer.interactive.util.DataLiveEvent
import com.ringer.interactive.util.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BriefContactViewState @Inject constructor(
    private val phones: PhonesInteractor,
    private val contacts: ContactsInteractor,
    private val navigations: NavigationsInteractor,
    private val permissions: PermissionsInteractor
) : BaseViewState() {
    val contactId = MutableLiveData<Long>()
    val contactImage = MutableLiveData<Uri>()
    val contactName = MutableLiveData<String?>()
    val contact = MutableLiveData<ContactAccount>()
    val isStarIconVisible = MutableLiveData<Boolean>()
    val isStarIconActivated = MutableLiveData<Boolean>()

    val callEvent = DataLiveEvent<String>()
    val confirmContactDeleteEvent = LiveEvent()

    private var _firstPhone: PhoneAccount? = null


    fun onContactId(contactId: Long) {
        this.contactId.value = contactId
        contacts.queryContact(contactId) {
            contactName.value = it?.name
            it?.photoUri?.let { uri -> contactImage.value = Uri.parse(uri) }
            isStarIconActivated.value = it?.starred == true
        }
        phones.getContactAccounts(contactId) {
            _firstPhone = it?.getOrNull(0)
        }
    }

    fun onActionCall() {
        _firstPhone?.number?.let(callEvent::call) ?: run {
            errorEvent.call(R.string.error_no_number_to_call)
        }
    }

    fun onActionSms() {
        _firstPhone?.number?.let(navigations::sendSMS)
    }

    fun onActionEdit() {
        contactId.value?.let(navigations::editContact)
    }

    fun onActionDelete() {
        confirmContactDeleteEvent.call()
    }

    fun onDeleteConfirmed() {
        permissions.runWithWriteContactsPermissions {
            contactId.value?.let(contacts::deleteContact)
            finishEvent.call()
        }
    }

    fun onActionStar(isActivate: Boolean) {
        contactId.value?.let { contacts.toggleContactFavorite(it, !isActivate) }
        isStarIconActivated.value = !isActivate
    }
}
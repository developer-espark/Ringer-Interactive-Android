package com.ringer.interactive.interactor.phoneaccounts

import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.model.PhoneAccount
import com.ringer.interactive.model.PhoneLookupAccount

interface PhonesInteractor : BaseInteractor<PhonesInteractor.Listener> {
    interface Listener

    fun lookupAccount(number: String?, callback: (PhoneLookupAccount?) -> Unit)
    fun getContactAccounts(contactId: Long, callback: (Array<PhoneAccount>?) -> Unit)
}
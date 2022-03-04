package com.ringer.interactive.interactor.phoneaccounts

import android.content.Context
import com.ringer.interactive.contentresolver.PhoneLookupContentResolver
import com.ringer.interactive.contentresolver.PhonesContentResolver
import com.ringer.interactive.interactor.base.BaseInteractorImpl
import com.ringer.interactive.model.PhoneAccount
import com.ringer.interactive.model.PhoneLookupAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.exceptions.OnErrorNotImplementedException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhonesInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseInteractorImpl<PhonesInteractor.Listener>(), PhonesInteractor {

    override fun lookupAccount(number: String?, callback: (PhoneLookupAccount?) -> Unit) {
        if (number == null || number.isEmpty()) {
            callback.invoke(PhoneLookupAccount.PRIVATE)
            return
        }
        try {
            PhoneLookupContentResolver(context, number).queryItems { phones ->
                callback.invoke(phones.getOrNull(0))
            }
        } catch (e: OnErrorNotImplementedException) {
            callback.invoke(null)
        }
    }

    override fun getContactAccounts(contactId: Long, callback: (Array<PhoneAccount>?) -> Unit) {
        PhonesContentResolver(context, contactId).queryItems {
            callback.invoke(it.toTypedArray())
        }
    }
}
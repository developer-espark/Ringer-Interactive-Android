package com.ringer.interactive.interactor.phoneaccounts

import android.content.Context
import com.chooloo.www.chooloolib.contentresolver.PhoneLookupContentResolver
import com.chooloo.www.chooloolib.contentresolver.PhonesContentResolver
import com.chooloo.www.chooloolib.model.PhoneAccount
import com.chooloo.www.chooloolib.model.PhoneLookupAccount
import com.chooloo.www.chooloolib.interactor.base.BaseInteractorImpl
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
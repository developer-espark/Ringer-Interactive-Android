package com.ringer.interactive.repository.phones

import androidx.lifecycle.LiveData
import com.chooloo.www.chooloolib.model.PhoneAccount

interface PhonesRepository {
    fun getPhones(contactId: Long? = null): LiveData<List<PhoneAccount>>
}
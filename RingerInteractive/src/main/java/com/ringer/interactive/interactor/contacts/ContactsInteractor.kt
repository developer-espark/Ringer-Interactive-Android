package com.ringer.interactive.interactor.contacts

import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.model.ContactAccount

interface ContactsInteractor : BaseInteractor<ContactsInteractor.Listener> {
    interface Listener

    fun deleteContact(contactId: Long)
    fun queryContact(contactId: Long, callback: (ContactAccount?) -> Unit)
    fun toggleContactFavorite(contactId: Long, isFavorite: Boolean)
    fun blockContact(contactId: Long, onSuccess: (() -> Unit)? = null)
    fun unblockContact(contactId: Long, onSuccess: (() -> Unit)? = null)
    fun getIsContactBlocked(contactId: Long, callback: (Boolean) -> Unit)
}
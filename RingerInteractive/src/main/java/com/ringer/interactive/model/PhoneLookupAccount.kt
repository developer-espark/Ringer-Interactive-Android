package com.ringer.interactive.model

import android.provider.ContactsContract


data class PhoneLookupAccount(
    val name: String?,
    val number: String? = null,
    val contactId: Long? = null,
    val photoUri: String? = null,
    val starred: Boolean? = false,
    val type: Int = ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
) {
    val displayString: String
        get() = name ?: (number ?: "Unknown")

    companion object {
        val PRIVATE = PhoneLookupAccount("Private Number")
    }
}

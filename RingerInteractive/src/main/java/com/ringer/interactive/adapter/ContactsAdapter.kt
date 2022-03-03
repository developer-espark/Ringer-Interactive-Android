package com.ringer.interactive.adapter

import android.net.Uri
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import com.ringer.interactive.model.ContactAccount
import com.ringer.interactive.model.ListData
import com.ringer.interactive.ui.widgets.listitem.ListItem
import com.ringer.interactive.util.initials
import javax.inject.Inject


open class ContactsAdapter @Inject constructor(
    animations: AnimationsInteractor,
    private val phones: PhonesInteractor,
    private val preferences: PreferencesInteractor,
) : ListAdapter<ContactAccount>(animations) {
    private var _withFavs: Boolean = true
    private var _withHeaders: Boolean = true

    var withFavs: Boolean
        get() = _withFavs
        set(value) {
            _withFavs = value
        }

    var withHeaders: Boolean
        get() = _withHeaders
        set(value) {
            _withHeaders = value
        }


    override fun onBindListItem(listItem: ListItem, item: ContactAccount) {
        listItem.apply {
            titleText = item.name
            isCompact = preferences.isCompact
            phones.getContactAccounts(item.id) { accounts ->
                captionText = accounts?.firstOrNull()?.number
            }

            setImageInitials(item.name?.initials())
            setImageUri(if (item.photoUri != null) Uri.parse(item.photoUri) else null)
        }
    }

    override fun convertDataToListData(items: List<ContactAccount>) =
        if (_withHeaders) ListData.fromContacts(items, _withFavs) else ListData(items)
}
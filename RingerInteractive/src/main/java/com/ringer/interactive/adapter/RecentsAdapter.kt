package com.ringer.interactive.adapter

import android.graphics.Color
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import com.ringer.interactive.interactor.recents.RecentsInteractor
import com.ringer.interactive.interactor.string.StringsInteractor
import com.ringer.interactive.model.ListData
import com.ringer.interactive.model.RecentAccount
import com.ringer.interactive.ui.widgets.listitem.ListItem
import com.ringer.interactive.util.getHoursString
import javax.inject.Inject

class RecentsAdapter @Inject constructor(
    animations: AnimationsInteractor,
    private val phones: PhonesInteractor,
    private val strings: StringsInteractor,
    private val recents: RecentsInteractor,
    private val preferences: PreferencesInteractor
) : ListAdapter<RecentAccount>(animations) {

    override fun onBindListItem(listItem: ListItem, item: RecentAccount) {
        listItem.apply {
            isCompact = preferences.isCompact
            captionText = if (item.date != null) context.getHoursString(item.date) else null
            phones.lookupAccount(item.number) {
                titleText = it?.name ?: item.number
                it?.let {
                    captionText = "$captionText · ${
                        strings.getString(Phone.getTypeLabelResource(it.type))
                    }"
                }
            }

            setImageBackgroundColor(Color.TRANSPARENT)
            setImageResource(recents.getCallTypeImage(item.type))
        }
    }

    override fun convertDataToListData(data: List<RecentAccount>) = ListData.fromRecents(data)
}
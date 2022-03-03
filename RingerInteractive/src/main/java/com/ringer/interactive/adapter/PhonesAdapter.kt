package com.ringer.interactive.adapter

import android.provider.ContactsContract.CommonDataKinds.Phone
import com.ringer.interactive.R
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.string.StringsInteractor
import com.ringer.interactive.model.ListData
import com.ringer.interactive.model.PhoneAccount
import com.ringer.interactive.ui.widgets.listitem.ListItem
import javax.inject.Inject

class PhonesAdapter @Inject constructor(
    animationsInteractor: AnimationsInteractor,
    private val strings: StringsInteractor
) : ListAdapter<PhoneAccount>(animationsInteractor) {
    override fun onBindListItem(listItem: ListItem, item: PhoneAccount) {
        listItem.apply {
            imageVisibility = false
            titleText = item.number
            isRightButtonVisible = true
            captionText = strings.getString(Phone.getTypeLabelResource(item.type))

            setTitleBold(true)
            setBackground(null)
            setRightButtonDrawable(R.drawable.round_call_20)
        }
    }

    override fun convertDataToListData(data: List<PhoneAccount>) = ListData.fromPhones(data)
}
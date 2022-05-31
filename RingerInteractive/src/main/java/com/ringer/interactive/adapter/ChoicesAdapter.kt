package com.ringer.interactive.adapter

import android.annotation.SuppressLint
import com.google.android.material.internal.ViewUtils
import com.ringer.interactive.R
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.color.ColorsInteractor
import com.ringer.interactive.model.ListData
import com.ringer.interactive.ui.widgets.listitem.ListItem
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class ChoicesAdapter @Inject constructor(
    animations: AnimationsInteractor,
    private val colors: ColorsInteractor,
) : ListAdapter<String>(animations) {

    override fun onBindListItem(listItem: ListItem, item: String) {
        listItem.apply {
            setTitleBold(true)
            setTitleColor(colors.getAttrColor(R.attr.colorOnSecondary))

            titleText = item
            imageVisibility = false
            imageSize = ViewUtils.dpToPx(context, 30).toInt()
        }
    }

    override fun convertDataToListData(data: List<String>) = ListData(data)
}
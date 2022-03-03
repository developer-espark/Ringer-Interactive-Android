package com.ringer.interactive.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MenuItem
import com.google.android.material.internal.ViewUtils
import com.ringer.interactive.R
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.color.ColorsInteractor
import com.ringer.interactive.model.ListData
import com.ringer.interactive.ui.widgets.listitem.ListItem
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class MenuAdapter @Inject constructor(
    animationsInteractor: AnimationsInteractor,
    private val colorsInteractor: ColorsInteractor
) : ListAdapter<MenuItem>(animationsInteractor) {

    override fun onBindListItem(listItem: ListItem, item: MenuItem) {
        listItem.apply {
            setBackgroundColor(Color.TRANSPARENT)
            setTitleTextAppearance(R.style.Chooloo_Text_Subtitle1)
            setImageTint(colorsInteractor.getColor(R.color.color_opposite))
            setTitleColor(colorsInteractor.getColor(R.color.color_opposite))

            paddingTop = 28
            paddingBottom = 28
            imageDrawable = item.icon
            titleText = item.title.toString()
            imageSize = ViewUtils.dpToPx(context, 30).toInt()
        }
    }

    override fun convertDataToListData(items: List<MenuItem>) = ListData(items)
}
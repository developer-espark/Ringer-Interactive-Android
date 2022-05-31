package com.ringer.interactive.interactor.drawable

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

interface DrawablesInteractor {
    interface Listener

    fun getDrawable(@DrawableRes res: Int): Drawable?
}
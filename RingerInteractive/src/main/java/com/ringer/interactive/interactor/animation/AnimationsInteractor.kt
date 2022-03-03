package com.ringer.interactive.interactor.animation

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface AnimationsInteractor {
    interface Listener

    fun focus(view: View)
    fun show(view: View, ifGone: Boolean)
    fun blink(view: View, totalDuration: Long, duration: Long = 400)
    fun hide(view: View, ifVisible: Boolean, goneOrInvisible: Boolean)

    fun animateRecyclerView(recyclerView: RecyclerView)
}
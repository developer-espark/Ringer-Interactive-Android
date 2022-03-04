package com.ringer.interactive.ui.widgets.listitem

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.ringer.interactive.R
import com.ringer.interactive.util.getAttrColor

class ListItemButton : ListItem {
    private val colorSecondary by lazy { context.getColor(R.color.gray) }
    private val colorOnSecondary by lazy { context.getColor(R.color.gray) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
    ) : super(context, attrs, defStyleRes) {
        image.setPadding(18)
        stateListAnimator = null
        imageTintList = ColorStateList.valueOf(colorOnSecondary)
        personLayout.backgroundTintList = ColorStateList.valueOf(colorSecondary)
        personLayout.background = ContextCompat.getDrawable(context, R.drawable.bubble_background)

        setTitleColor(colorOnSecondary)
    }

    override fun setPaddingMode(isCompact: Boolean, isEnabled: Boolean) {
        personLayout.setPadding(
            dimenSpacingSmall + 20,
            dimenSpacingSmall,
            dimenSpacingSmall + 20,
            dimenSpacingSmall
        )
        header.setPadding(
            if (isEnabled) dimenSpacing else 0,
            dimenSpacingSmall,
            if (isEnabled) dimenSpacing else 0,
            if (isCompact) dimenSpacingSmall - 10 else dimenSpacingSmall
        )
    }
}
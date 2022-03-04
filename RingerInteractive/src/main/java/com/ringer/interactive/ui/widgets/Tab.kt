package com.ringer.interactive.ui.widgets

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.ringer.interactive.R
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.util.getAttrColor
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomViewStyleable", "Recycle")
class Tab : AppCompatTextView {
    private val enabledColor by lazy { context.getColor(R.color.gray) }
    private val disabledColor by lazy { context.getAttrColor(R.attr.colorLightBackground) }

    @Inject lateinit var animationsInteractor: AnimationsInteractor


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
    ) : super(context, attrs, defStyleRes) {
        textSize = 28f
        text = text.toString().replaceFirstChar { it.uppercase(Locale.ROOT) }
        typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)

        setTextColor(enabledColor)
    }


    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        setColor(if (activated) enabledColor else disabledColor)
        if (activated) {
            animateAttention()
        }
    }


    private fun animateAttention() {
        animationsInteractor.show(this, true)
    }

    private fun setColor(@ColorInt color: Int) {
        ValueAnimator.ofObject(ArgbEvaluator(), currentTextColor, color).apply {
            addUpdateListener { setTextColor(it.animatedValue as Int) }
            start()
        }
    }
}
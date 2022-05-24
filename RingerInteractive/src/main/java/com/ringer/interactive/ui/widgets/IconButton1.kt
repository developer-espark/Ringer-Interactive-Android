package com.ringer.interactive.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat.getDrawable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ViewUtils
import com.ringer.interactive.R
import com.ringer.interactive.util.getAttrColor

@SuppressLint("CustomViewStyleable", "WrongConstant", "RestrictedApi")
class IconButton1 : FloatingActionButton {
    @DrawableRes
    private var _iconDefault: Int? = null

    @DrawableRes
    private var _iconActivated: Int? = null

    private var _imageTintList: ColorStateList?
    private var _backgroundTintList: ColorStateList?
    private val _alterActivatedBackground: Boolean
    private var _backgroundTintList1: ColorStateList?

    private val dimenPadding by lazy { ViewUtils.dpToPx(context, 15).toInt() }
    private val dimenSizeBig by lazy { ViewUtils.dpToPx(context, 70).toInt() }
    private val dimenSizeMini by lazy { ViewUtils.dpToPx(context, 10).toInt() }
    private val dimenSizeDefault by lazy { ViewUtils.dpToPx(context, 60).toInt() }
    private val dimenCornerSize by lazy { context.resources.getDimension(R.dimen.image_size_small) }
    private val colorOnSecondary1 by lazy { context.getColor(android.R.color.white) }
    private val colorOnSecondary by lazy { context.getColor(R.color.green_foreground1) }
    var iconDefault: Int?
        get() = _iconDefault
        set(value) {
            _iconDefault = value
            refreshResources()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
    ) : super(context, attrs.apply {

    }, defStyleRes) {
        context.obtainStyledAttributes(attrs, R.styleable.Chooloo_IconButton, defStyleRes, 0).also {
            size = it.getInteger(R.styleable.Chooloo_IconButton_size, 0)
            _iconDefault = it.getResourceId(R.styleable.Chooloo_IconButton_icon, NO_ID)
            _iconActivated = it.getResourceId(R.styleable.Chooloo_IconButton_activatedIcon, NO_ID)
            _alterActivatedBackground =
                it.getBoolean(R.styleable.Chooloo_IconButton_alterActivatedBackground, true)
        }.recycle()

        compatElevation = 0f
        _backgroundTintList = backgroundTintList
        _backgroundTintList1 = ColorStateList.valueOf(colorOnSecondary1)
        imageTintList = imageTintList ?: ColorStateList.valueOf(colorOnSecondary)
        _imageTintList = imageTintList
        _imageTintList?.defaultColor?.let { rippleColor = it }
        shapeAppearanceModel =
            shapeAppearanceModel.toBuilder().setAllCornerSizes(dimenCornerSize).build()
        customSize = when (size) {
            SIZE_BIG -> dimenSizeBig
            SIZE_MINI -> dimenSizeMini
            else -> dimenSizeDefault
        }

        if (_iconDefault != NO_ID) {
            _iconDefault?.let { setImageDrawable(getDrawable(context, it)) }
        }
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        refreshResources()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        imageAlpha = if (isEnabled) 255 else 40
    }

    private fun refreshResources() {
        if (_iconActivated != NO_ID) {
            Log.e("iconActivated",""+_iconActivated)
            (if (isActivated) _iconActivated else _iconDefault)?.let { setImageResource(it) }


        }
        if (_alterActivatedBackground) {
            imageTintList = if (isActivated) _backgroundTintList1 else _imageTintList
//            backgroundTintList = if (isActivated) _imageTintList else _backgroundTintList
        }
    }


    companion object {
        private const val SIZE_BIG = 2
    }
}
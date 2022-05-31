package com.ringer.interactive.ui.dialpad

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.ringer.interactive.databinding.DialpadInCallBinding
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.ui.base.BaseFragment
import com.ringer.interactive.ui.widgets.DialpadWhiteKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class DialpadInCallFragment @Inject constructor() : BaseFragment<DialpadViewState>() {
    override val contentView by lazy { binding.root }
    override val viewState: DialpadViewState by activityViewModels()

    @Inject
    lateinit var animationsInteractor: AnimationsInteractor

    protected val binding by lazy { DialpadInCallBinding.inflate(layoutInflater) }
    private var keypadPressed = ""
    override fun onSetup() {
        binding.apply {

            dialpadButtonDelete.isVisible = false

            dialpadEditText.apply {
                isClickable = false
                isLongClickable = false
                isCursorVisible = false
                isFocusableInTouchMode = false
            }

            dialpadEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (dialpadEditText.text != null && !dialpadEditText.text.toString()
                            .isNullOrEmpty()
                    ) {
                        dialpadEditText.visibility = View.VISIBLE
                        dialpadButtonDelete.visibility = View.VISIBLE
                    } else {
                        dialpadEditText.visibility = View.GONE
                        dialpadButtonDelete.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })

            dialpadButtonDelete.setOnClickListener {
                keypadPressed = keypadPressed.dropLast(1)
                dialpadEditText.setText(keypadPressed)
            }

            View.OnClickListener {
                viewState.onCharClick((it as DialpadWhiteKey).char)
                keypadPressed += it.char
                dialpadEditText.setText(keypadPressed)
            }
                .also {
                    key0.setOnClickListener(it)
                    key1.setOnClickListener(it)
                    key2.setOnClickListener(it)
                    key3.setOnClickListener(it)
                    key4.setOnClickListener(it)
                    key5.setOnClickListener(it)
                    key6.setOnClickListener(it)
                    key7.setOnClickListener(it)
                    key8.setOnClickListener(it)
                    key9.setOnClickListener(it)
                    keyHex.setOnClickListener(it)
                    keyStar.setOnClickListener(it)
                }

            View.OnLongClickListener {
                viewState.onLongKeyClick((it as DialpadWhiteKey).char)
            }
                .also {
                    key0.setOnLongClickListener(it)
                    key1.setOnLongClickListener(it)
                    key2.setOnLongClickListener(it)
                    key3.setOnLongClickListener(it)
                    key4.setOnLongClickListener(it)
                    key5.setOnLongClickListener(it)
                    key6.setOnLongClickListener(it)
                    key7.setOnLongClickListener(it)
                    key8.setOnLongClickListener(it)
                    key9.setOnLongClickListener(it)
                    keyHex.setOnLongClickListener(it)
                    keyStar.setOnLongClickListener(it)
                }
        }

    }
}
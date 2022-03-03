package com.ringer.interactive.ui.settings

import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.ringer.interactive.R
import com.ringer.interactive.interactor.dialog.DialogsInteractor
import com.ringer.interactive.ui.base.BaseMenuFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class SettingsFragment @Inject constructor() : BaseMenuFragment() {
    override val title by lazy { getString(R.string.settings) }
    override val viewState: SettingsViewState by viewModels()

    @Inject lateinit var dialogs: DialogsInteractor


    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            menuResList.observe(this@SettingsFragment, this@SettingsFragment::setMenuResList)

            askForColorEvent.observe(this@SettingsFragment) { ev ->
                ev.ifNew?.let { dialogs.askForColor(it, viewState::onColorResponse) }
            }

            askForCompactEvent.observe(this@SettingsFragment) {
                it.ifNew?.let {
                    dialogs.askForBoolean(R.string.hint_compact_mode, viewState::onCompactResponse)
                }
            }

            askForThemeModeEvent.observe(this@SettingsFragment) {
                it.ifNew?.let { dialogs.askForThemeMode(viewState::onThemeModeResponse) }
            }

            askForAnimationsEvent.observe(this@SettingsFragment) {
                it.ifNew?.let {
                    dialogs.askForBoolean(R.string.hint_animations, viewState::onAnimationsResponse)
                }
            }
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem) {
        viewState.onMenuItemClick(menuItem)
    }
}
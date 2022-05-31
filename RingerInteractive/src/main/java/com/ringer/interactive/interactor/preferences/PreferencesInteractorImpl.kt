package com.ringer.interactive.interactor.preferences

import com.ringer.interactive.R
import com.ringer.interactive.interactor.base.BaseInteractorImpl
import com.ringer.interactive.util.PreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesInteractorImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseInteractorImpl<PreferencesInteractor.Listener>(),
    PreferencesInteractor {

    override var isAskSim: Boolean
        get() = preferencesManager.getBoolean(
            R.string.pref_key_ask_sim,
            R.bool.pref_default_value_should_ask_sim
        )
        set(value) {
            preferencesManager.putBoolean(R.string.pref_key_ask_sim, value)
        }

    override var isCompact: Boolean
        get() = preferencesManager.getBoolean(
            R.string.pref_key_compact,
            R.bool.pref_default_value_compact
        )
        set(value) {
            preferencesManager.putBoolean(R.string.pref_key_compact, value)
        }

    override var isAnimations: Boolean
        get() = preferencesManager.getBoolean(
            R.string.pref_key_animations,
            R.bool.pref_default_value_animations
        )
        set(value) {
            preferencesManager.putBoolean(R.string.pref_key_animations, value)
        }

    override var isShowBlocked: Boolean
        get() = preferencesManager.getBoolean(
            R.string.pref_key_show_blocked,
            R.bool.pref_default_value_show_blocked
        )
        set(value) {
            preferencesManager.putBoolean(R.string.pref_key_show_blocked, value)
        }

    override var isDialpadTones: Boolean
        get() = preferencesManager.getBoolean(
            R.string.pref_key_dialpad_tones,
            R.bool.pref_default_value_dialpad_tones
        )
        set(value) {
            preferencesManager.putBoolean(R.string.pref_key_dialpad_tones, value)
        }

    override var isDialpadVibrate: Boolean
        get() = preferencesManager.getBoolean(
            R.string.pref_key_dialpad_vibrate,
            R.bool.pref_default_value_dialpad_vibrate
        )
        set(value) {
            preferencesManager.putBoolean(R.string.pref_key_dialpad_vibrate, value)
        }

    override var defaultPage: PreferencesInteractor.Companion.Page
        get() = PreferencesInteractor.Companion.Page.fromKey(preferencesManager.getString(R.string.pref_key_default_page))
        set(value) {
            preferencesManager.putString(R.string.pref_key_default_page, value.key)
        }

    override var themeMode: PreferencesInteractor.Companion.ThemeMode
        get() = PreferencesInteractor.Companion.ThemeMode.fromKey(preferencesManager.getString(R.string.pref_key_theme_mode))
        set(value) {
            preferencesManager.putString(R.string.pref_key_theme_mode, value.key)
        }

    override var accentTheme: PreferencesInteractor.Companion.AccentTheme
        get() = PreferencesInteractor.Companion.AccentTheme.fromKey(preferencesManager.getString(R.string.pref_key_color))
        set(value) {
            preferencesManager.putString(R.string.pref_key_color, value.key)
        }
}
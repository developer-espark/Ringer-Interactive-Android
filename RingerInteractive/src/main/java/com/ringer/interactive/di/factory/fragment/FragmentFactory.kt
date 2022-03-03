package com.ringer.interactive.di.factory.fragment

import androidx.annotation.StringRes
import com.ringer.interactive.ui.base.BaseChoicesFragment
import com.ringer.interactive.ui.briefcontact.BriefContactFragment
import com.ringer.interactive.ui.callitems.CallItemsFragment
import com.ringer.interactive.ui.contact.ContactFragment
import com.ringer.interactive.ui.contacts.ContactsFragment
import com.ringer.interactive.ui.contacts.ContactsSuggestionsFragment
import com.ringer.interactive.ui.dialer.DialerFragment
import com.ringer.interactive.ui.dialpad.DialpadFragment
import com.ringer.interactive.ui.phones.PhonesFragment
import com.ringer.interactive.ui.prompt.PromptFragment
import com.ringer.interactive.ui.recent.RecentFragment
import com.ringer.interactive.ui.recents.RecentsFragment
import com.ringer.interactive.ui.recents.RecentsHistoryFragment
import com.ringer.interactive.ui.settings.SettingsFragment

interface FragmentFactory {
    fun getContactFragment(): ContactFragment
    fun getDialpadFragment(): DialpadFragment
    fun getSettingsFragment(): SettingsFragment
    fun getContactsFragment(): ContactsFragment
    fun getCallItemsFragment(): CallItemsFragment
    fun getRecentFragment(recentId: Long): RecentFragment
    fun getDialerFragment(text: String? = null): DialerFragment
    fun getPhonesFragment(contactId: Long? = null): PhonesFragment
    fun getRecentsFragment(filter: String? = null): RecentsFragment
    fun getContactsSuggestionsFragment(): ContactsSuggestionsFragment
    fun getBriefContactFragment(contactId: Long): BriefContactFragment
    fun getPromptFragment(title: String, subtitle: String): PromptFragment
    fun getRecentsHistoryFragment(filter: String? = null): RecentsHistoryFragment
    fun getChoicesFragment(
        @StringRes titleRes: Int,
        @StringRes subtitleRes: Int?,
        choices: List<String>
    ): BaseChoicesFragment
}
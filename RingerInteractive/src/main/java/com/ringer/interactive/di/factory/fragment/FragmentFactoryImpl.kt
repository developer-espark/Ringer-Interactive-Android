package com.ringer.interactive.di.factory.fragment

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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FragmentFactoryImpl @Inject constructor() : FragmentFactory {
    override fun getContactFragment() = ContactFragment()
    override fun getDialpadFragment() = DialpadFragment()
    override fun getSettingsFragment() = SettingsFragment()
    override fun getContactsFragment() = ContactsFragment()
    override fun getCallItemsFragment() = CallItemsFragment()
    override fun getContactsSuggestionsFragment() = ContactsSuggestionsFragment()
    override fun getDialerFragment(text: String?) = DialerFragment.newInstance(text)
    override fun getRecentFragment(recentId: Long) = RecentFragment.newInstance(recentId)
    override fun getRecentsFragment(filter: String?) = RecentsFragment.newInstance(filter)
    override fun getPhonesFragment(contactId: Long?) = PhonesFragment.newInstance(contactId)
    override fun getBriefContactFragment(contactId: Long) =
        BriefContactFragment.newInstance(contactId)

    override fun getPromptFragment(title: String, subtitle: String) =
        PromptFragment.newInstance(title, subtitle)

    override fun getRecentsHistoryFragment(filter: String?) =
        RecentsHistoryFragment.newInstance(filter)

    override fun getChoicesFragment(
        titleRes: Int,
        subtitleRes: Int?,
        choices: List<String>
    ) = BaseChoicesFragment.newInstance(titleRes, subtitleRes, choices)

}
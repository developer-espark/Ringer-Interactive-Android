package com.ringer.interactive.model

import com.chooloo.www.chooloolib.util.getRelativeDateString

data class ListData<DataType>(
    val items: List<DataType> = arrayListOf(),
    val headersToCounts: Map<String, Int> = emptyMap()
) {
    companion object {
        fun fromContacts(
            contacts: List<ContactAccount>,
            withFavs: Boolean = false
        ): ListData<ContactAccount> {
            val headersToCounts = contacts.groupingBy { it.name?.get(0).toString() }.eachCount()
            if (withFavs) {
                val favs = contacts.filter { it.starred }
                if (favs.isNotEmpty()) {
                    return ListData(
                        items = ArrayList(favs + contacts),
                        headersToCounts = mapOf(Pair("★", favs.size)) + headersToCounts
                    )
                }
            }
            return ListData(contacts, headersToCounts)
        }

        fun fromRecents(recents: List<RecentAccount>) = ListData(
            recents,
            recents.groupingBy { getRelativeDateString(it.date) }.eachCount()
        )

        fun fromPhones(phones: List<PhoneAccount>): ListData<PhoneAccount> {
            val phones = phones.toList().distinctBy { it.normalizedNumber }
            return ListData(phones, mapOf(Pair("Phones", phones.size)))
        }
    }
}
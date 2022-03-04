package com.ringer.interactive.ui.recent

import android.Manifest.permission.WRITE_CALL_LOG
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.R
import com.ringer.interactive.interactor.blocked.BlockedInteractor
import com.ringer.interactive.interactor.drawable.DrawablesInteractor
import com.ringer.interactive.interactor.navigation.NavigationsInteractor
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import com.ringer.interactive.interactor.recents.RecentsInteractor
import com.ringer.interactive.ui.base.BaseViewState
import com.ringer.interactive.util.DataLiveEvent
import com.ringer.interactive.util.LiveEvent
import com.ringer.interactive.util.getElapsedTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentViewState @Inject constructor(
    private val phones: PhonesInteractor,
    private val recents: RecentsInteractor,
    private val blocked: BlockedInteractor,
    private val drawables: DrawablesInteractor,
    private val preferences: PreferencesInteractor,
    private val navigations: NavigationsInteractor,
    private val permissions: PermissionsInteractor
) :
    BaseViewState() {

    val name = MutableLiveData<String?>()
    val recentId = MutableLiveData(0L)
    val image = MutableLiveData<Drawable?>()
    val timeString = MutableLiveData<String?>()
    val durationString = MutableLiveData<String?>()
    val isContactVisible = MutableLiveData(false)
    val isAddContactVisible = MutableLiveData(false)
    val isBlockButtonVisible = MutableLiveData(false)
    val isBlockButtonActivated = MutableLiveData(false)

    val callEvent = DataLiveEvent<String>()
    val confirmRecentDeleteEvent = LiveEvent()
    val showRecentEvent = DataLiveEvent<Long>()
    val showContactEvent = DataLiveEvent<Long>()
    val showHistoryEvent = DataLiveEvent<String>()

    private val _recent by lazy { recents.queryRecent(recentId.value!!) }


    override fun attach() {
        super.attach()
        if (_recent == null) return

        timeString.value = _recent!!.relativeTime
        durationString.value =
            if (_recent!!.duration > 0L) getElapsedTimeString(_recent!!.duration) else null
        image.value =
            drawables.getDrawable(recents.getCallTypeImage(_recent!!.type))
        name.value =
            if (_recent!!.cachedName?.isNotEmpty() == true) _recent!!.cachedName else _recent!!.number

        phones.lookupAccount(_recent!!.number) {
            isContactVisible.value = it?.name != null
            isAddContactVisible.value = it?.name == null
        }

        isBlockButtonVisible.value = preferences.isShowBlocked
        if (preferences.isShowBlocked) {
            permissions.runWithDefaultDialer {
                isBlockButtonActivated.value = blocked.isNumberBlocked(_recent!!.number)
            }
        }
    }

    fun onActionSms() {
        _recent?.let { navigations.sendSMS(it.number) }
    }

    fun onActionCall() {
        _recent?.let { callEvent.call(it.number) }
    }

    fun onActionDelete() {
        permissions.runWithPermissions(arrayOf(WRITE_CALL_LOG), {
            confirmRecentDeleteEvent.call()
        }, {
            errorEvent.call(R.string.error_no_permissions_edit_call_log)
        })
    }

    fun onActionAddContact() {
        _recent?.let { navigations.addContact(it.number) }
    }

    fun onActionOpenContact() {
        _recent?.let {
            phones.lookupAccount(it.number) { account ->
                account?.contactId?.let(showContactEvent::call)
            }
        }
    }

    fun onActionShowHistory() {
        _recent?.let { showHistoryEvent.call(it.number) }
    }

    fun onActionBlock(isBlock: Boolean) {
        permissions.runWithDefaultDialer(R.string.error_not_default_dialer_blocked) {
            _recent?.number?.let {
                if (isBlock) {
                    blocked.blockNumber(it)
                } else {
                    blocked.unblockNumber(it)
                }
                isBlockButtonActivated.value = isBlock
            }
        }
    }

    fun onRecentClick(recentId: Long) {
        showRecentEvent.call(recentId)
    }

    fun onConfirmDelete() {
        _recent?.let {
            recents.deleteRecent(it.id)
            finishEvent.call()
        }
    }
}
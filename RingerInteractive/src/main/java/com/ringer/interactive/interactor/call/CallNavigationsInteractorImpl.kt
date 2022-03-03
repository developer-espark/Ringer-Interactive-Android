package com.ringer.interactive.interactor.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.VoicemailContract
import android.telecom.TelecomManager
import androidx.annotation.RequiresPermission
import com.ringer.interactive.R
import com.ringer.interactive.interactor.base.BaseInteractorImpl
import com.ringer.interactive.interactor.dialog.DialogsInteractor
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import com.ringer.interactive.interactor.sim.SimsInteractor
import com.ringer.interactive.model.SimAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CallNavigationsInteractorImpl @Inject constructor(
    private val sims: SimsInteractor,
    private val dialogs: DialogsInteractor,
    private val telecomManager: TelecomManager,
    private val permissions: PermissionsInteractor,
    private val preferences: PreferencesInteractor,
    @ApplicationContext private val context: Context
) : BaseInteractorImpl<CallNavigationsInteractor.Listener>(), CallNavigationsInteractor {
    override fun callVoicemail() {
        permissions.runWithDefaultDialer(R.string.error_not_default_dialer_call) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.fromParts("voicemail", "", null)
            context.startActivity(intent)
        }
    }

//    @RequiresPermission(allOf = [Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE])
//    override fun call(number: String) {
//        permissions.runWithDefaultDialer(null, {
//            sims.getIsMultiSim { isMultiSim ->
//                if (preferences.isAskSim && isMultiSim) {
//                    dialogs.askForSim { call(it, number) }
//                } else {
//                    sims.getSimAccounts { call(it[0], number) }
//                }
//            }
//        }, {
//            call(null, number)
//        })
//    }

    @RequiresPermission(allOf = [Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE])
    override fun call(number: String) {
        telecomManager.placeCall(Uri.parse("tel:${Uri.encode(number)}"), Bundle())
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun call(simAccount: SimAccount?, number: String) {
        val extras = Bundle()
        simAccount?.phoneAccountHandle?.let {
            extras.putParcelable(VoicemailContract.EXTRA_PHONE_ACCOUNT_HANDLE, it)
        }
        telecomManager.placeCall(Uri.parse("tel:${Uri.encode(number)}"), extras)
    }
}

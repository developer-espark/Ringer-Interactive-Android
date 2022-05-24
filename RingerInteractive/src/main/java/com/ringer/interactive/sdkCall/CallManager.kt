package com.ringer.interactive.sdkCall

import android.telecom.Call
import android.telecom.VideoProfile


object CallManager {
    var sCall: Call? = null

    fun answer() {
        sCall?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun reject() {
        if (sCall?.state == Call.STATE_RINGING) {
            sCall?.reject(false, null)
        } else {
            sCall?.disconnect()
        }
    }

    fun hold(isHold: Boolean) {
        if (isHold) {
            sCall?.hold()
        } else {
            sCall?.unhold()
        }
    }

    fun keypad(c: Char) {
        sCall?.playDtmfTone(c)
        sCall?.stopDtmfTone()
    }




  /*  private fun getSelection(activity: BaseActivity) {

        try {
            val phoneAccountHandleList: List<PhoneAccountHandle> =
                TelecomUtil().getTelecomManager(activity)!!.callCapablePhoneAccounts
//            val simCard: Int = getSimSelection(activity)

//            Log.e("simCard", "" + simCard)


            // Create call intent
            val callIntent =
                Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(number.toString())))
            if (phoneAccountHandleList.isNotEmpty()) {
                callIntent.putExtra(
                    TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,
                    phoneAccountHandleList[simCard]
                )
            }
            //            // Handle sim card selection
            Log.d("simcard %s", ""+simCard)
            if (simCard != -1) callIntent.putExtra("com.android.phone.extra.slot", simCard)
            //            // Start the call
            activity.startActivity(callIntent)
        } catch (e: SecurityException) {
            Toast.makeText(
                activity,
                "Couldn't make a call due to security reasons",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: NullPointerException) {
            Toast.makeText(activity, "Couldnt make a call, no phone number", Toast.LENGTH_LONG)
                .show()
        }
    }*/


}
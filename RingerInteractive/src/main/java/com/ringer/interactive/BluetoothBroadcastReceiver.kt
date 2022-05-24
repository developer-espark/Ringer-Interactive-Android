package com.ringer.interactive

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager.MODE_IN_CALL
import android.media.AudioManager.MODE_NORMAL

class BluetoothBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        when (intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_CONNECTED -> {
                        audioManager.apply {
                            isBluetoothScoOn = false
                            stopBluetoothSco()
                            mode = MODE_NORMAL
                        }
                    }
                    else -> {
                        audioManager.apply {
                            mode = AudioManager.MODE_IN_CALL
                            isBluetoothScoOn = true
                            startBluetoothSco()
                            mode = MODE_IN_CALL
                        }
                    }
                }
            }
        }
    }
}
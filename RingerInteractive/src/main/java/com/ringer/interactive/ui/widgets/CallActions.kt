package com.ringer.interactive.ui.widgets

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import com.ringer.interactive.BluetoothBroadcastReceiver
import com.ringer.interactive.R
import com.ringer.interactive.databinding.CallActionsBinding


class CallActions : MotionLayout {
    private val _binding: CallActionsBinding
    private var _isBluetoothActivated: Boolean = false
    private var _isDialerActivated: Boolean = false
    private var _callActionsListener: CallActionsListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        _callActionsListener = getEmptyListener()

        _binding = CallActionsBinding.inflate(LayoutInflater.from(context), this, true)
//        _binding.callActionSwap.setOnClickListener { _callActionsListener?.onSwapClick() }
        _binding.callActionHold.setOnClickListener { _callActionsListener?.onHoldClick() }
        _binding.callActionMute.setOnClickListener { _callActionsListener?.onMuteClick() }
        _binding.callActionMerge.setOnClickListener { _callActionsListener?.onMergeClick() }
        _binding.callActionSpeaker.setOnClickListener { _callActionsListener?.onSpeakerClick() }
        _binding.callActionAddCall.setOnClickListener { _callActionsListener?.onAddCallClick() }
        _binding.mLinearActionBluetooth.setOnClickListener {



            try {
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (mBluetoothAdapter.isEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                    }

                    mBluetoothAdapter.disable()
                    _isBluetoothActivated = false
                    if (_isBluetoothActivated) {
                        _binding.callActionBluetooth.visibility = View.VISIBLE
                        _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_green))
//                _binding.callActionSpeaker.iconDefault = R.drawable.round_bluetooth_audio_24
                    } else {
                        _binding.callActionBluetooth.visibility = View.VISIBLE

                        _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_new))
                        _binding.callActionSpeaker.iconDefault = R.drawable.round_volume_down_24
                    }

                }else{
                    mBluetoothAdapter.enable()
                    _isBluetoothActivated = true
                    if (_isBluetoothActivated) {
                        _binding.callActionBluetooth.visibility = View.VISIBLE
                        _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_green))
//                _binding.callActionSpeaker.iconDefault = R.drawable.round_bluetooth_audio_24
                    } else {
                        _binding.callActionBluetooth.visibility = View.VISIBLE
                        _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_new))
                        _binding.callActionSpeaker.iconDefault = R.drawable.round_volume_down_24
                    }

                }
            }catch (e : Exception){
                Toast.makeText(context,"Enable Bluetooth > Edit Settings > Allow Nearby Devices",Toast.LENGTH_SHORT).show()
            }


            _callActionsListener?.onBluetoothClick()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            context.registerReceiver(BluetoothBroadcastReceiver(), filter)
        }
        _binding.callActionKeyboard.setOnClickListener { _callActionsListener?.onKeyBoardClick() }
    }


    private fun transitionLayoutTo(constraintRes: Int) {
        if (_binding.root.currentState != constraintRes) {
            _binding.root.setTransition(_binding.root.currentState, constraintRes)
            _binding.root.transitionToEnd()
        }
    }


    var isHoldActivated: Boolean
        get() = _binding.callActionHold.isActivated
        set(value) {
            _binding.callActionHold.isActivated = value
        }


    var isMuteActivated: Boolean
        get() = _binding.callActionMute.isActivated
        set(value) {
            _binding.callActionMute.isActivated = value
        }

    var isSpeakerActivated: Boolean
        get() = _binding.callActionSpeaker.isActivated
        set(value) {
            _binding.callActionSpeaker.isActivated = value
        }
/*
    var isBluetoothActivated: Boolean
        get() = _isBluetoothActivated
        set(value) {
            _isBluetoothActivated = value
            if (value) {
                _binding.callActionBluetooth.visibility = View.VISIBLE
                _binding.callActionBluetooth.isClickable = true
//                _binding.callActionBluetooth.visibility = View.VISIBLE
//                _binding.callActionBluetooth.isClickable = true
                _binding.callActionBluetooth.iconDefault = R.drawable.img_bluetooth_green
            } else {
                if (!_isDialerActivated) {
                    _binding.callActionBluetooth.visibility = View.VISIBLE
                }
                _binding.callActionBluetooth.isClickable = false 
                _binding.callActionBluetooth.iconDefault = R.drawable.img_bluetooth_green
                *//*_binding.callActionSpeaker.iconDefault = R.drawable.round_volume_down_24*//*
            }
        }*/

    var isBluetoothActivated: Boolean
        get() = _isBluetoothActivated
        set(value) {
            _isBluetoothActivated = value



            if (value) {
                _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_green))
            } else {
                _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_new))
            }
        }

    var isDialerActivated: Boolean
        get() = _isDialerActivated
        set(value) {
            _isDialerActivated = value
            if (value) {
                _binding.callActionBluetooth.visibility = View.GONE
            } else {
                if (_isBluetoothActivated) {
                    _binding.callActionBluetooth.visibility = View.VISIBLE
                    _binding.callActionBluetooth.isClickable = true
                    _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_green))
//                _binding.callActionSpeaker.iconDefault = R.drawable.round_bluetooth_audio_24
                } else {

                    _binding.callActionBluetooth.visibility = View.VISIBLE
                    _binding.callActionBluetooth.isClickable = false
                    _binding.callActionBluetooth.setImageDrawable(resources.getDrawable(R.drawable.img_bluetooth_new))
                    /*_binding.callActionSpeaker.iconDefault = R.drawable.round_volume_down_24*/
                }
            }
        }


    var isHoldEnabled: Boolean
        get() = _binding.callActionHold.isEnabled
        set(value) {
            _binding.callActionHold.isEnabled = value
        }

    var isMuteEnabled: Boolean
        get() = _binding.callActionMute.isEnabled
        set(value) {
            _binding.callActionMute.isEnabled = value
        }

    /*   var isSwapEnabled: Boolean
           get() = _binding.callActionSwap.isEnabled
           set(value) {
               _binding.callActionSwap.isEnabled = value
           }*/

    var isMergeEnabled: Boolean
        get() = _binding.callActionMerge.isEnabled
        set(value) {
            _binding.callActionMerge.isEnabled = value
        }

    var isSpeakerEnabled: Boolean
        get() = _binding.callActionSpeaker.isEnabled
        set(value) {
            _binding.callActionSpeaker.isEnabled = value
        }


    fun showSingleCallUI() {
        transitionLayoutTo(R.id.constraint_set_single_call)
//        _binding.callActionSwap.visibility = GONE
        _binding.callActionMerge.visibility = GONE
    }

    fun showMultiCallUI() {
        transitionLayoutTo(R.id.constraint_set_multi_call)
//        _binding.callActionSwap.visibility = GONE
        _binding.callActionMerge.visibility = VISIBLE
    }

    fun setCallActionsListener(callActionsListener: CallActionsListener?) {
        _callActionsListener = callActionsListener
    }


    private fun getEmptyListener() = object : CallActionsListener {
        override fun onHoldClick() {}
        override fun onMuteClick() {}
        override fun onSwapClick() {}
        override fun onMergeClick() {}
        override fun onKeypadClick() {}
        override fun onSpeakerClick() {}
        override fun onAddCallClick() {}
        override fun onBluetoothClick() {}
        override fun onKeyBoardClick() {}
    }


    interface CallActionsListener {
        fun onHoldClick()
        fun onMuteClick()
        fun onSwapClick()
        fun onMergeClick()
        fun onKeypadClick()
        fun onSpeakerClick()
        fun onAddCallClick()
        fun onBluetoothClick() {}
        fun onKeyBoardClick()

    }
}
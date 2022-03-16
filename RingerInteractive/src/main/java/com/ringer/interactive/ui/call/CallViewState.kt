package com.ringer.interactive.ui.call

import android.net.Uri
import android.os.Build
import android.telecom.Call.Details.CAPABILITY_HOLD
import android.telecom.Call.Details.CAPABILITY_MUTE
import android.telecom.PhoneAccountHandle
import android.telecom.PhoneAccountSuggestion
import android.telecom.TelecomManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.R
import com.ringer.interactive.interactor.audio.AudiosInteractor
import com.ringer.interactive.interactor.callaudio.CallAudiosInteractor
import com.ringer.interactive.interactor.calls.CallsInteractor
import com.ringer.interactive.interactor.color.ColorsInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.interactor.proximity.ProximitiesInteractor
import com.ringer.interactive.interactor.string.StringsInteractor
import com.ringer.interactive.model.Call
import com.ringer.interactive.model.CantHoldCallException
import com.ringer.interactive.model.CantMergeCallException
import com.ringer.interactive.model.CantSwapCallException
import com.ringer.interactive.service.CallService
import com.ringer.interactive.ui.base.BaseViewState
import com.ringer.interactive.ui.widgets.CallActions
import com.ringer.interactive.util.DataLiveEvent
import com.ringer.interactive.util.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CallViewState @Inject constructor(
    private val telecomManger: TelecomManager,
    private val calls: CallsInteractor,
    private val audios: AudiosInteractor,
    private val colors: ColorsInteractor,
    private val phones: PhonesInteractor,
    private val strings: StringsInteractor,
    private val disposables: CompositeDisposable,
    private val callAudios: CallAudiosInteractor,
    private val proximities: ProximitiesInteractor,
) :
    BaseViewState(),
    CallsInteractor.Listener,
    CallAudiosInteractor.Listener,
    CallActions.CallActionsListener {

    var name = MutableLiveData<String?>()
    var number = MutableLiveData<String?>()
    val imageRes = MutableLiveData<Int>()
    val uiState = MutableLiveData<UIState>()
    val imageURI = MutableLiveData<Uri?>(null)
    val bannerText = MutableLiveData<String>()
    val stateText = MutableLiveData<String?>()
    val elapsedTime = MutableLiveData<Long>()
    val stateTextColor = MutableLiveData<Int>()

    val isHoldEnabled = MutableLiveData<Boolean>()
    val isMuteEnabled = MutableLiveData<Boolean>()
    val isSwapEnabled = MutableLiveData<Boolean>()
    val isMergeEnabled = MutableLiveData<Boolean>()
    val isMuteActivated = MutableLiveData<Boolean>()
    val isHoldActivated = MutableLiveData<Boolean>()

    val isKeyPadeEnable = MutableLiveData<Boolean>()
    val isKeyPadeActivated = MutableLiveData<Boolean>()
    val isManageEnabled = MutableLiveData(false)
    val isSpeakerEnabled = MutableLiveData(true)
    val isSpeakerActivated = MutableLiveData<Boolean>()
    val isBluetoothActivated = MutableLiveData<Boolean>()

    val showDialerEvent = LiveEvent()
    val showContactEvent = LiveEvent()
    val showDialpadEvent = LiveEvent()
    val askForRouteEvent = LiveEvent()
    val showCallManagerEvent = LiveEvent()
    val selectPhoneHandleEvent = DataLiveEvent<List<PhoneAccountHandle>>()
    val selectPhoneSuggestionEvent = DataLiveEvent<List<PhoneAccountSuggestion>>()

    private var _currentCallId: String? = null


    override fun attach() {
        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { displayCallTime() })

        CallService.sIsActivityActive = true

        proximities.acquire()
        calls.registerListener(this@CallViewState)
        callAudios.registerListener(this@CallViewState)
        calls.mainCall?.let {
            onCallChanged(it)
            onMainCallChanged(it)
            callAudios.isMuted?.let(this@CallViewState::onMuteChanged)
            callAudios.audioRoute?.let(this@CallViewState::onAudioRouteChanged)
        }


        isManageEnabled.value = false
    }

    override fun detach() {
        proximities.release()
        CallService.sIsActivityActive = false
    }

    fun onAnswerClick() {
        _currentCallId?.let { calls.answerCall(it) }
    }

    fun onRejectClick() {
        _currentCallId?.let { calls.rejectCall(it) }
    }

    override fun onSwapClick() {
        try {
            _currentCallId?.let { calls.swapCall(it) }
        } catch (e: CantSwapCallException) {
            errorEvent.call(R.string.error_cant_swap_calls)
            e.printStackTrace()
        }
    }

    override fun onHoldClick() {
        try {
            _currentCallId?.let { calls.toggleHold(it) }
        } catch (e: CantHoldCallException) {
            errorEvent.call(R.string.error_cant_hold_call)
            e.printStackTrace()
        }
    }

    override fun onMuteClick() {
        callAudios.isMuted = !isMuteActivated.value!!
    }

    override fun onMergeClick() {
        try {
            _currentCallId?.let { calls.mergeCall(it) }
        } catch (e: CantMergeCallException) {
            errorEvent.call(R.string.error_cant_merge_call)
            e.printStackTrace()
        }
    }

    override fun onKeypadClick() {
        showDialpadEvent.call()
    }

    override fun onAddCallClick() {
        showDialpadEvent.call()
//        asdasd
    }

    override fun onBluetoothClick() {
        callAudios.apply {
            if (supportedAudioRoutes.contains(CallAudiosInteractor.AudioRoute.BLUETOOTH)) {
                isBluetoothOn = !isBluetoothActivated.value!!
                isSpeakerOn = !isSpeakerActivated.value!!

//                askForRouteEvent.call()

            }

        }
    }

    override fun onKeyBoardClick() {



    }


    override fun onSpeakerClick() {
        callAudios.apply {
           /* if (supportedAudioRoutes.contains(CallAudiosInteractor.AudioRoute.BLUETOOTH)) {
                Log.e("AudioRoot","AudioRoot")
                askForRouteEvent.call()

            } else {
                isSpeakerOn = !isSpeakerActivated.value!!
            }*/
            isSpeakerOn = !isSpeakerActivated.value!!
            isBluetoothOn = !isBluetoothActivated.value!!
//            askForRouteEvent.call()
        }
    }

    fun onCharKey(char: Char) {
        _currentCallId?.let { calls.invokeCallKey(it, char) }
    }


    override fun onNoCalls() {
        audios.audioMode = AudiosInteractor.AudioMode.NORMAL
        finishEvent.call()
    }

    override fun onCallChanged(call: Call) {
        if (calls.getFirstState(Call.State.HOLDING)?.id == _currentCallId) {
            bannerText.value = null
        } else if (call.isHolding && _currentCallId != call.id && !call.isInConference) {
            phones.lookupAccount(call.number) {
                bannerText.value = String.format(
                    strings.getString(R.string.explain_is_on_hold),
                    it?.displayString ?: call.number
                )
            }
        } else if (calls.getStateCount(Call.State.HOLDING) == 0) {
            bannerText.value = null
        }
    }

    override fun onMainCallChanged(call: Call) {
        _currentCallId = call.id

        if (call.isEnterprise) {
            imageRes.value = R.drawable.round_business_24
        }

        if (call.isIncoming) {
            uiState.value = UIState.INCOMING
        }

        if (call.isConference) {
            name.value = strings.getString(R.string.conference)
            number.value = ""
        } else {
            phones.lookupAccount(call.number) { account ->
                account?.photoUri?.let { imageURI.value = Uri.parse(it) }
                name.value = account?.displayString ?: call.number
                if (account!!.displayString == ""){
                    name.value = call.number

                }else{

                    name.value = account.displayString
                    number.value = call.number
                }

            }
        }

        isHoldActivated.value = call.isHolding
        isManageEnabled.value = call.isConference
        isHoldEnabled.value = call.isCapable(CAPABILITY_HOLD)
        isMuteEnabled.value = call.isCapable(CAPABILITY_MUTE)
//        isSwapEnabled.value = call.isCapable(CAPABILITY_SWAP_CONFERENCE)
        stateText.value = strings.getString(call.state.stringRes)

        when {
            call.isIncoming -> uiState.value = UIState.INCOMING
            calls.isMultiCall -> uiState.value = UIState.MULTI
            else -> uiState.value = UIState.ACTIVE
        }

        when (call.state) {
            Call.State.INCOMING, Call.State.ACTIVE -> stateTextColor.value =
                colors.getColor(R.color.green_foreground)
            Call.State.HOLDING, Call.State.DISCONNECTING, Call.State.DISCONNECTED -> stateTextColor.value =
                colors.getColor(R.color.red_foreground)
        }

        if (call.state == Call.State.SELECT_PHONE_ACCOUNT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                selectPhoneSuggestionEvent.call(call.suggestedPhoneAccounts)
            } else {
                selectPhoneHandleEvent.call(call.availablePhoneAccounts)
            }
        }
    }


    override fun onMuteChanged(isMuted: Boolean) {
        isMuteActivated.value = isMuted
    }

    override fun onAudioRouteChanged(audioRoute: CallAudiosInteractor.AudioRoute) {
        isSpeakerActivated.value = audioRoute == CallAudiosInteractor.AudioRoute.SPEAKER
        isBluetoothActivated.value = audioRoute == CallAudiosInteractor.AudioRoute.BLUETOOTH
    }

    private fun displayCallTime() {
        calls.mainCall?.let {
            elapsedTime.value = if (it.isStarted) it.durationTimeMilis else null
        }
    }

    fun onManageClick() {
        showCallManagerEvent.call()
    }

    fun onAudioRoutePicked(audioRoute: CallAudiosInteractor.AudioRoute) {
        callAudios.audioRoute = audioRoute
    }

    fun onPhoneAccountHandleSelected(phoneAccountHandle: PhoneAccountHandle) {
        calls.mainCall?.selectPhoneAccount(phoneAccountHandle)
    }

    enum class UIState {
        MULTI,
        ACTIVE,
        INCOMING
    }
}
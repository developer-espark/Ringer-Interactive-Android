package com.ringer.interactive.interactor.audio

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.AudioManager.*
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.view.KeyEvent
import com.ringer.interactive.interactor.base.BaseInteractorImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudiosInteractorImpl @Inject constructor(
    private val vibrator: Vibrator,
    private val audioManager: AudioManager
) : BaseInteractorImpl<AudiosInteractor.Listener>(), AudiosInteractor {

    override var isMuted: Boolean
        get() = audioManager.isMicrophoneMute
        set(value) {
            audioManager.isMicrophoneMute = value
        }

    override var isSilent: Boolean
        get() = audioManager.ringerMode in arrayOf(RINGER_MODE_SILENT, RINGER_MODE_VIBRATE)
        set(value) {
            audioManager.ringerMode = if (value) RINGER_MODE_SILENT else RINGER_MODE_NORMAL
        }

    override var isSpeakerOn: Boolean
        get() = audioManager.isSpeakerphoneOn
        set(value) {
            audioManager.isSpeakerphoneOn = value
        }

    override var audioMode: AudiosInteractor.AudioMode
        get() = AudiosInteractor.AudioMode.values().associateBy(AudiosInteractor.AudioMode::mode)
            .getOrDefault(audioManager.mode, AudiosInteractor.AudioMode.NORMAL)
        set(value) {
            audioManager.mode = value.mode
        }


    override fun playTone(tone: Int) {
        playTone(tone, TONE_LENGTH_MS)
    }

    override fun playToneByChar(char: Char) {
        playTone(sCharToTone.getOrDefault(char, -1))
    }

    override fun playToneByKey(keyCode: Int) {
        playTone(sKeyToTone.getOrDefault(keyCode, -1))
    }

    override fun playTone(tone: Int, durationMs: Int) {
        if (tone != -1 && !isSilent) {
            synchronized(sToneGeneratorLock) {
                ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME).startTone(
                    tone, durationMs
                ) // Start the new tone (will stop any playing tone)
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun vibrate(millis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(millis)
        }
    }


    companion object {
        const val TONE_LENGTH_MS = 150 // The length of DTMF tones in milliseconds
        const val DIAL_TONE_STREAM_TYPE = STREAM_DTMF
        const val TONE_RELATIVE_VOLUME =
            80 // The DTMF tone volume relative to other sounds in the stream

        private val sToneGeneratorLock = Any()
        private val sKeyToTone by lazy {
            HashMap<Int, Int>().apply {
                put(KeyEvent.KEYCODE_0, ToneGenerator.TONE_DTMF_0)
                put(KeyEvent.KEYCODE_1, ToneGenerator.TONE_DTMF_1)
                put(KeyEvent.KEYCODE_2, ToneGenerator.TONE_DTMF_2)
                put(KeyEvent.KEYCODE_3, ToneGenerator.TONE_DTMF_3)
                put(KeyEvent.KEYCODE_4, ToneGenerator.TONE_DTMF_4)
                put(KeyEvent.KEYCODE_5, ToneGenerator.TONE_DTMF_5)
                put(KeyEvent.KEYCODE_6, ToneGenerator.TONE_DTMF_6)
                put(KeyEvent.KEYCODE_7, ToneGenerator.TONE_DTMF_7)
                put(KeyEvent.KEYCODE_8, ToneGenerator.TONE_DTMF_8)
                put(KeyEvent.KEYCODE_9, ToneGenerator.TONE_DTMF_9)
                put(KeyEvent.KEYCODE_POUND, ToneGenerator.TONE_DTMF_P)
                put(KeyEvent.KEYCODE_STAR, ToneGenerator.TONE_DTMF_S)
            }
        }
        private val sCharToTone by lazy {
            HashMap<Char, Int>().apply {
                put('0', ToneGenerator.TONE_DTMF_0)
                put('1', ToneGenerator.TONE_DTMF_1)
                put('2', ToneGenerator.TONE_DTMF_2)
                put('3', ToneGenerator.TONE_DTMF_3)
                put('4', ToneGenerator.TONE_DTMF_4)
                put('5', ToneGenerator.TONE_DTMF_5)
                put('6', ToneGenerator.TONE_DTMF_6)
                put('7', ToneGenerator.TONE_DTMF_7)
                put('8', ToneGenerator.TONE_DTMF_8)
                put('9', ToneGenerator.TONE_DTMF_9)
                put('#', ToneGenerator.TONE_DTMF_P)
                put('*', ToneGenerator.TONE_DTMF_S)
            }
        }
    }
}
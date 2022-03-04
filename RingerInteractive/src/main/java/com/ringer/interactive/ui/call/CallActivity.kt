package com.ringer.interactive.ui.call

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.ContactsContract
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.ringer.interactive.R
import com.ringer.interactive.databinding.CallBinding
import com.ringer.interactive.di.factory.fragment.FragmentFactory
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.dialog.DialogsInteractor
import com.ringer.interactive.interactor.prompt.PromptsInteractor
import com.ringer.interactive.interactor.screen.ScreensInteractor
import com.ringer.interactive.ui.base.BaseActivity
import com.ringer.interactive.ui.dialpad.DialpadViewState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class CallActivity : BaseActivity<CallViewState>() {
    override val contentView by lazy { binding.root }
    override val viewState: CallViewState by viewModels()

    private val dialpadViewState: DialpadViewState by viewModels()
    private val binding by lazy { CallBinding.inflate(layoutInflater) }

    @Inject
    lateinit var screens: ScreensInteractor
    @Inject
    lateinit var dialogs: DialogsInteractor
    @Inject
    lateinit var prompts: PromptsInteractor
    @Inject
    lateinit var animations: AnimationsInteractor
    @Inject
    lateinit var fragmentFactory: FragmentFactory


    @SuppressLint("Range")
    override fun onSetup() {
        screens.showWhenLocked()

        viewState.apply {


            imageRes.observe(this@CallActivity, binding.callImage::setImageResource)
//            imageRes.observe(this@CallActivity, binding.callImage1::setImageResource)



            stateTextColor.observe(this@CallActivity, binding.callStateText::setTextColor)

            name.observe(this@CallActivity) {
                binding.callNameText.text = it
            }

            imageURI.observe(this@CallActivity) {
                if (it != null){

                    binding.callImage.setScaleType(ImageView.ScaleType.FIT_XY)
                    binding.callImage.setImageURI(it)

//                    binding.callImage1.setImageURI(it)
                }else{
                    binding.callImage.layoutParams.height = 10
                    binding.callImage.layoutParams.width = 10



                    binding.callImage.setImageDrawable(resources.getDrawable(R.drawable.download))

/*                    binding.callImage1.layoutParams.height = 400
                    binding.callImage1.layoutParams.width = 400
                    binding.callImage1.setImageDrawable(resources.getDrawable(R.drawable.download))*/
                }


            }

            elapsedTime.observe(this@CallActivity) {
                it?.let {
                    animations.show(binding.callTimeText, true)
                    binding.callTimeText.text = DateUtils.formatElapsedTime(it / 1000)
                } ?: run {
                    animations.hide(binding.callTimeText, ifVisible = true, goneOrInvisible = false)
                }
            }

            bannerText.observe(this@CallActivity) {
                it?.let {
                    binding.callBanner.text = it
                    if (binding.callBanner.visibility != View.VISIBLE) {
                        binding.callBanner.visibility = View.VISIBLE
                        animations.show(binding.callBanner, true)
                        animations.focus(binding.callBanner)
                    }
                } ?: run {
                    animations.hide(
                        binding.callBanner,
                        ifVisible = true,
                        goneOrInvisible = false
                    )
                }
            }

            stateText.observe(this@CallActivity) {
                val old = binding.callStateText.text.toString()
                binding.callStateText.text = it
                if (old != it) {
                    animations.focus(binding.callStateText)
                }
            }

            uiState.observe(this@CallActivity) {
                when (it) {
                    CallViewState.UIState.MULTI -> {
                        showActiveLayout()
                        binding.callActions.showMultiCallUI()
                    }
                    CallViewState.UIState.ACTIVE -> {
                        showActiveLayout()
                        binding.callActions.showSingleCallUI()
                    }
                    CallViewState.UIState.INCOMING -> {
                        transitionLayoutTo(R.id.constraint_set_incoming_call)
                    }
                }
            }

            isHoldEnabled.observe(this@CallActivity) {
                binding.callActions.isHoldEnabled = it
            }

            isMuteEnabled.observe(this@CallActivity) {
                binding.callActions.isMuteEnabled = it
            }

         /*   isSwapEnabled.observe(this@CallActivity) {
                binding.callActions.isSwapEnabled = it
            }*/

            isMergeEnabled.observe(this@CallActivity) {
                binding.callActions.isMergeEnabled = it
            }

        /*    isManageEnabled.observe(this@CallActivity) {
                binding.callManageButton.isVisible = it
            }*/

            isSpeakerEnabled.observe(this@CallActivity) {
                binding.callActions.isSpeakerEnabled = it
            }

            isMuteActivated.observe(this@CallActivity) {
                binding.callActions.isMuteActivated = it
            }

            isHoldActivated.observe(this@CallActivity) {
                binding.callActions.isHoldActivated = it
            }

            isSpeakerActivated.observe(this@CallActivity) {
                binding.callActions.isSpeakerActivated = it
            }

            isBluetoothActivated.observe(this@CallActivity) {
                binding.callActions.isBluetoothActivated = it
            }

            askForRouteEvent.observe(this@CallActivity) {
                it.ifNew?.let { dialogs.askForRoute(viewState::onAudioRoutePicked) }
            }

            showDialerEvent.observe(this@CallActivity) {
                it.ifNew?.let { prompts.showFragment(fragmentFactory.getDialerFragment()) }
            }

            showContactEvent.observe(this@CallActivity) {
                it.ifNew?.let { prompts.showFragment(fragmentFactory.getContactFragment()) }
            }

            showDialpadEvent.observe(this@CallActivity) {
                /*it.ifNew?.let { prompts.showFragment(fragmentFactory.getContactsFragment()) }*/
                val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
                startActivity(intent)
            }



            selectPhoneHandleEvent.observe(this@CallActivity) {
                it.ifNew?.let {
                    dialogs.askForPhoneAccountHandle(it) {
                        viewState.onPhoneAccountHandleSelected(it)
                    }
                }
            }

            selectPhoneSuggestionEvent.observe(this@CallActivity) {
                it.ifNew?.let {
                    dialogs.askForPhoneAccountSuggestion(it) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            viewState.onPhoneAccountHandleSelected(it.phoneAccountHandle)
                        }
                    }
                }
            }
        }


        binding.apply {
            callActions.setCallActionsListener(viewState)

            callAnswerButton.setOnClickListener {
                viewState.onAnswerClick()
            }

            callRejectButton.setOnClickListener {
                viewState.onRejectClick()
            }
/*
            callManageButton.setOnClickListener {
                viewState.onManageClick()
            }*/
        }

        dialpadViewState.char.observe(this@CallActivity, viewState::onCharKey)
    }


    private fun showActiveLayout() {
        transitionLayoutTo(R.id.constraint_set_active_call)
        if (binding.callActions.visibility != View.VISIBLE) {
            animations.show(binding.callActions, true)
        }
    }

    private fun transitionLayoutTo(constraintRes: Int) {
        if (binding.root.currentState != constraintRes) {
            binding.root.setTransition(binding.root.currentState, constraintRes)
            binding.root.transitionToEnd()
        }
    }
}

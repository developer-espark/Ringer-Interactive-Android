package com.ringer.interactive.ui.call

import android.R.attr.x
import android.R.attr.y
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.ContactsContract
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
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
import kotlinx.android.synthetic.main.call_actions.view.*
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class CallActivity : BaseActivity<CallViewState>() {
    override val contentView by lazy { binding.root }
    override val viewState: CallViewState by viewModels()


    private val dialpadViewState: DialpadViewState by viewModels()
    private val binding by lazy { CallBinding.inflate(layoutInflater) }

    var isKeyBoard : Boolean = false

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

            binding.callActions.call_action_keyboard.setOnClickListener {

                if (!isKeyBoard){
                    isKeyBoard = true
                    binding.edtKeypad.visibility = View.VISIBLE
                    binding.edtKeypad.requestFocus()
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInputFromWindow(
                        it.applicationWindowToken,
                        InputMethodManager.SHOW_FORCED, 0
                    )


                }else{
                    isKeyBoard = false
                    binding.edtKeypad.visibility = View.GONE
                    binding.edtKeypad.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(it.windowToken, 0)

                }
            }

            binding.edtKeypad.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyBoard = false
                    binding.edtKeypad.visibility = View.GONE
                    binding.edtKeypad.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.edtKeypad.windowToken, 0)
                    return@OnEditorActionListener true
                }
                false
            })

            imageRes.observe(this@CallActivity, binding.callImage::setImageResource)
//            imageRes.observe(this@CallActivity, binding.callImage1::setImageResource)


            stateTextColor.observe(this@CallActivity, binding.callStateText::setTextColor)

            name.observe(this@CallActivity) {


                binding.callNameText.isSelected = true


                if (it!!.contains(resources.getString(R.string.conference))) {

                    binding.callNameText.text = resources.getString(R.string.conference)
                } else {

                    if (TextUtils.isDigitsOnly(it)) {

                        var phoneNumberCode = PhoneNumberWithoutCountryCode(it.toString())
                        var numberCode = "(" + phoneNumberCode!!.substring(
                            0,
                            3
                        ) + ") " + phoneNumberCode.substring(
                            3,
                            6
                        ) + "-" + phoneNumberCode.substring(6)

                        binding.callNameText.text = numberCode
                    } else {

                        binding.callNameText.text = it
                    }

                }

            }
            number.observe(this@CallActivity) {

                if (binding.callNameText.text.contains(resources.getString(R.string.conference))) {

                    binding.callNumber.visibility = View.GONE
                } else {


                    if (it.equals("")) {
                        binding.callNumber.visibility = View.GONE
                    } else {


                        var phoneNumberCode = PhoneNumberWithoutCountryCode(it.toString())


                        var numberCode = "(" + phoneNumberCode!!.substring(
                            0,
                            3
                        ) + ") " + phoneNumberCode.substring(
                            3,
                            6
                        ) + "-" + phoneNumberCode.substring(6)




                        binding.callNumber.visibility = View.VISIBLE


                        binding.callNumber.text = numberCode
                    }
                }

            }


            imageURI.observe(this@CallActivity) {
                if (it != null) {

                    binding.callImage.setScaleType(ImageView.ScaleType.FIT_XY)
                    binding.callImage.setImageURI(it)

//                    binding.lun.background = resources.getDrawable(R.drawable.call_bg)

                    binding.callNameText.setTextColor(resources.getColor(android.R.color.white))
                    binding.callNumber.setTextColor(resources.getColor(android.R.color.white))

                } else {

                    binding.callImage.background = resources.getDrawable(R.drawable.gradient)
                  /*  binding.callImage.layoutParams.height = 10
                    binding.callImage.layoutParams.width = 10

                    binding.callImage.setImageDrawable(resources.getDrawable(R.drawable.download))*/
                    binding.callNameText.setTextColor(resources.getColor(android.R.color.white))
                    binding.callNumber.setTextColor(resources.getColor(android.R.color.white))
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
                if (!it.equals("Incoming Call")){

                    binding.edtKeypad.visibility = View.GONE
                    animations.show(binding.call,true)
                }
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

    fun PhoneNumberWithoutCountryCode(phoneNumberWithCountryCode: String): String? {
        val compile: Pattern = Pattern.compile(
            "\\+(?:998|996|995|994|993|992|977|976|975|974|973|972|971|970|968|967|966|965|964|963|962|961|960|886|880|856|855|853|852|850|692|691|690|689|688|687|686|685|683|682|681|680|679|678|677|676|675|674|673|672|670|599|598|597|595|593|592|591|590|509|508|507|506|505|504|503|502|501|500|423|421|420|389|387|386|385|383|382|381|380|379|378|377|376|375|374|373|372|371|370|359|358|357|356|355|354|353|352|351|350|299|298|297|291|290|269|268|267|266|265|264|263|262|261|260|258|257|256|255|254|253|252|251|250|249|248|246|245|244|243|242|241|240|239|238|237|236|235|234|233|232|231|230|229|228|227|226|225|224|223|222|221|220|218|216|213|212|211|98|95|94|93|92|91|90|86|84|82|81|66|65|64|63|62|61|60|58|57|56|55|54|53|52|51|49|48|47|46|45|44\\D?1624|44\\D?1534|44\\D?1481|44|43|41|40|39|36|34|33|32|31|30|27|20|7|1\\D?939|1\\D?876|1\\D?869|1\\D?868|1\\D?849|1\\D?829|1\\D?809|1\\D?787|1\\D?784|1\\D?767|1\\D?758|1\\D?721|1\\D?684|1\\D?671|1\\D?670|1\\D?664|1\\D?649|1\\D?473|1\\D?441|1\\D?345|1\\D?340|1\\D?284|1\\D?268|1\\D?264|1\\D?246|1\\D?242|1)\\D?"
        )

        return phoneNumberWithCountryCode.replace(compile.pattern().toRegex(), "")
    }

}

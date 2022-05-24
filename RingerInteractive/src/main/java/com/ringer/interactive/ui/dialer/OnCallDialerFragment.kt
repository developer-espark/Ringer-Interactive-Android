package com.ringer.interactive.ui.dialer

import android.content.Context
import com.ringer.interactive.di.factory.fragment.FragmentFactory
import com.ringer.interactive.interactor.call.CallNavigationsInteractor
import com.ringer.interactive.ui.dialpad.DialpadInCallFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OnCallDialerFragment @Inject constructor() : DialpadInCallFragment() {
    @Inject
    lateinit var callNavigations: CallNavigationsInteractor

    @Inject
    lateinit var fragmentFactory: FragmentFactory


    override fun onSetup() {
        super.onSetup()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    companion object {
        private const val ARG_NUMBER = "number"

        fun newInstance() = OnCallDialerFragment().apply {
        }
    }
}
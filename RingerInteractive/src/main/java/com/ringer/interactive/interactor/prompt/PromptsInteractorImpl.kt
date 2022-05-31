package com.ringer.interactive.interactor.prompt

import androidx.fragment.app.FragmentManager
import com.ringer.interactive.interactor.base.BaseInteractorImpl
import com.ringer.interactive.ui.base.BaseFragment
import com.ringer.interactive.ui.base.BottomFragment
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PromptsInteractorImpl @Inject constructor(
    private val fragmentManager: FragmentManager
) : BaseInteractorImpl<PromptsInteractor.Listener>(),
    PromptsInteractor {

    override fun showFragment(fragment: BaseFragment<*>, tag: String?) {
        BottomFragment(fragment).show(fragmentManager, tag)
    }
}
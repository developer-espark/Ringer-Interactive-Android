package com.ringer.interactive.interactor.string

import android.content.Context
import com.ringer.interactive.util.baseobservable.BaseObservable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringsInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseObservable<StringsInteractor.Listener>(), StringsInteractor {

    override fun getString(stringRes: Int) = context.getString(stringRes)
}
package com.ringer.interactive.ui.recents

import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.repository.recents.RecentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentsHistoryViewState @Inject constructor(
    permissions: PermissionsInteractor,
    recentsRepository: RecentsRepository
) : RecentsViewState(recentsRepository, permissions)
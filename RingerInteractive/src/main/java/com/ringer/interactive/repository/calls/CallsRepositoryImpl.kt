package com.ringer.interactive.repository.calls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ringer.interactive.model.Call
import com.ringer.interactive.service.CallService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallsRepositoryImpl @Inject constructor() : CallsRepository {
    val calls = MutableLiveData<List<Call>>()

    override fun getCalls(): LiveData<List<Call>> = CallService.sInstance?.calls ?: calls
}
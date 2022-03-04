package com.ringer.interactive.repository.calls

import androidx.lifecycle.LiveData
import com.ringer.interactive.model.Call

interface CallsRepository {
    fun getCalls(): LiveData<List<Call>>
}
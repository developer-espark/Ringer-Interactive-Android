package com.ringer.interactive.interactor.blocked

import android.content.ContentValues
import android.content.Context
import android.provider.BlockedNumberContract
import android.provider.BlockedNumberContract.BlockedNumbers
import com.ringer.interactive.interactor.base.BaseInteractorImpl
import com.ringer.interactive.util.annotation.RequiresDefaultDialer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseInteractorImpl<BlockedInteractor.Listener>(), BlockedInteractor {

    @RequiresDefaultDialer
    override fun isNumberBlocked(number: String) =
        BlockedNumberContract.isBlocked(context, number)

    @RequiresDefaultDialer
    override fun blockNumber(number: String) {
        if (isNumberBlocked(number)) return
        val contentValues = ContentValues()
        contentValues.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
        context.contentResolver.insert(BlockedNumbers.CONTENT_URI, contentValues)
    }

    @RequiresDefaultDialer
    override fun unblockNumber(number: String) {
        if (!isNumberBlocked(number)) return
        val contentValues = ContentValues()
        contentValues.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
        context.contentResolver.insert(BlockedNumbers.CONTENT_URI, contentValues)?.also {
            context.contentResolver.delete(it, null, null)
        }
    }
}
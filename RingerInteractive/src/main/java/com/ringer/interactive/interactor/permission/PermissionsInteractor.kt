package com.ringer.interactive.interactor.permission

import androidx.annotation.StringRes
import com.ringer.interactive.interactor.base.BaseInteractor
import com.ringer.interactive.ui.permissions.PermissionRequestActivity

interface PermissionsInteractor : BaseInteractor<PermissionsInteractor.Listener> {
    interface Listener

    val isDefaultDialer: Boolean


    fun entryDefaultDialerResult(granted: Boolean)
    fun entryPermissionResult(responses: List<PermissionRequestActivity.Companion.PermissionResult>, requestCode: Int)

    fun hasSelfPermission(permission: String): Boolean
    fun hasSelfPermissions(permissions: Array<String>): Boolean

    fun checkDefaultDialer(callback: (Boolean) -> Unit)
    fun checkPermissions(
        vararg permissions: String,
        callback: (List<PermissionRequestActivity.Companion.PermissionResult>) -> Unit
    )

    fun runWithPermissions(
        permissions: Array<String>,
        grantedCallback: () -> Unit,
        deniedCallback: (() -> Unit)? = null
    )

    fun runWithDefaultDialer(
        @StringRes errorMessageRes: Int? = null,
        callback: () -> Unit,
    )

    fun runWithDefaultDialer(
        @StringRes errorMessageRes: Int? = null,
        grantedCallback: () -> Unit,
        notGrantedCallback: (() -> Unit)? = null
    )

    fun runWithReadCallLogPermissions(callback: (Boolean) -> Unit)
    fun runWithReadContactsPermissions(callback: (Boolean) -> Unit)
    fun runWithWriteContactsPermissions(callback: (Boolean) -> Unit)
    fun runWithWriteCallLogPermissions(callback: (Boolean) -> Unit)
    fun runWithWriteCallPhonePermissions(callback: (Boolean) -> Unit)
    fun runWithWriteReadPhoneStatePermissions(callback: (Boolean) -> Unit)
}
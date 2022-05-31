package com.ringer.interactive.ui.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.ui.base.BaseActivity
import com.ringer.interactive.ui.base.BaseViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionRequestActivity : BaseActivity<BaseViewState>() {
    override val contentView: View? = null
    override val viewState: BaseViewState by viewModels()

    @Inject lateinit var permissions: PermissionsInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            requestPermissions()
        }
    }

    override fun onSetup() {}

    private fun requestPermissions() {
        val permissions = intent?.getStringArrayExtra(PERMISSIONS_ARGUMENT_KEY) ?: arrayOf()
        val requestCode = intent?.getIntExtra(REQUEST_CODE_ARGUMENT_KEY, -1) ?: -1
        when {
            permissions.isNotEmpty() && requestCode != -1 -> ActivityCompat.requestPermissions(
                this,
                permissions,
                requestCode
            )
            else -> finishWithResult()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionResults = grantResults.zip(permissions).map { (grantResult, permission) ->
            val state = when {
                grantResult == PackageManager.PERMISSION_GRANTED -> PermissionState.GRANTED
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission
                ) -> PermissionState.DENIED_TEMP
                else -> PermissionState.DENIED_PERM
            }
            PermissionResult(permission, state)
        }

        finishWithResult(permissionResults)
    }

    private fun finishWithResult(permissionResult: List<PermissionResult> = listOf()) {
        val requestCode = intent?.getIntExtra(REQUEST_CODE_ARGUMENT_KEY, -1) ?: -1
        permissions.entryPermissionResult(permissionResult, requestCode)
        finish()
    }

    companion object {
        const val PERMISSIONS_ARGUMENT_KEY = "PERMISSIONS_ARGUMENT_KEY"
        const val REQUEST_CODE_ARGUMENT_KEY = "REQUEST_CODE_ARGUMENT_KEY"

        class PermissionResult(val permission: String, val state: PermissionState)
        enum class PermissionState { GRANTED, DENIED_TEMP, DENIED_PERM }
    }
}
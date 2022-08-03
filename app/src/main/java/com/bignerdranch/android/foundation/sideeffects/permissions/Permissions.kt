package com.bignerdranch.android.foundation.sideeffects.permissions

import com.bignerdranch.android.foundation.sideeffects.permissions.plugin.PermissionStatus
import com.bignerdranch.android.foundation.sideeffects.permissions.plugin.PermissionsPlugin

interface Permissions {

    fun hasPermissions(permission: String): Boolean

    suspend fun requestPermission(permission: String): PermissionStatus

}
package com.ovais.blinkcode.core.manager

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun interface PermissionManager {
    fun hasPermission(permission: String): Boolean
}

class DefaultPermissionManager(
    private val context: Context
) : PermissionManager {

    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
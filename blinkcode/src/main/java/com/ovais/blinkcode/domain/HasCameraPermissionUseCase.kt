package com.ovais.blinkcode.domain

import android.Manifest
import com.ovais.blinkcode.core.manager.PermissionManager
import com.ovais.blinkcode.utils.usecase.UseCase

fun interface HasCameraPermissionUseCase : UseCase<Boolean>

class DefaultHasCameraPermissionUseCase(
    private val permissionManager: PermissionManager
) : HasCameraPermissionUseCase {
    override fun invoke(): Boolean {
        return permissionManager.hasPermission(Manifest.permission.CAMERA)
    }
}
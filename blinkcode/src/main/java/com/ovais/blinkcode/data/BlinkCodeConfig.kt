package com.ovais.blinkcode.data

import androidx.compose.ui.graphics.Color

data class BlinkCodeConfig(
    val scanningEnabled: Boolean = true,
    val creationEnabled: Boolean = true,
    val loggingEnabled: Boolean = true,
    val qrSize: Size = Size(400, 400),
    val defaultColor: Color = Color.Black,
    val autoSaveGeneratedFiles: Boolean = false,
    val scanIntervalMillis: Long = 500
)

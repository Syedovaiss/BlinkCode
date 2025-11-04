package com.ovais.blinkcode.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.ovais.blinkcode.data.Size
import com.ovais.blinkcode.domain.GenerateBarcodeInput
import com.ovais.blinkcode.domain.GenerateBarcodeUseCase
import com.ovais.blinkcode.domain.GenerateQRCodeInput
import com.ovais.blinkcode.domain.GenerateQRCodeUseCase
import kotlinx.coroutines.launch
import timber.log.Timber

class BarcodeGeneratorViewModel(
    private val generateQRCodeUseCase: GenerateQRCodeUseCase,
    private val generateBarcodeUseCase: GenerateBarcodeUseCase,
) : ViewModel() {

    var generatedBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<Throwable?>(null)
        private set

    /**
     * Generates a QR code with the given content and size
     */
    fun generateQRCode(
        content: String,
        size: Size,
        foregroundColor: Color = Color.Black,
        backgroundColor: Color = Color.White
    ) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val result = generateQRCodeUseCase(
                    GenerateQRCodeInput(
                        content = content,
                        size = size,
                        foregroundColor = foregroundColor,
                        backgroundColor = backgroundColor
                    )
                )
                result.fold(
                    onSuccess = { bitmap ->
                        generatedBitmap = bitmap
                    },
                    onFailure = { throwable ->
                        error = throwable
                        Timber.e(throwable, "Failed to generate QR code")
                    }
                )
            } catch (e: Exception) {
                error = e
                Timber.e(e, "Error generating QR code")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Generates a barcode with the given content, format, and size
     */
    fun generateBarcode(
        content: String,
        format: BarcodeFormat,
        size: Size,
        foregroundColor: Color = Color.Black,
        backgroundColor: Color = Color.White
    ) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val result = generateBarcodeUseCase(
                    GenerateBarcodeInput(
                        content = content,
                        format = format,
                        size = size,
                        foregroundColor = foregroundColor,
                        backgroundColor = backgroundColor
                    )
                )
                result.fold(
                    onSuccess = { bitmap ->
                        generatedBitmap = bitmap
                    },
                    onFailure = { throwable ->
                        error = throwable
                        Timber.e(throwable, "Failed to generate barcode")
                    }
                )
            } catch (e: Exception) {
                error = e
                Timber.e(e, "Error generating barcode")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Clears the generated bitmap
     */
    fun clearGeneratedBitmap() {
        generatedBitmap = null
        error = null
    }
}


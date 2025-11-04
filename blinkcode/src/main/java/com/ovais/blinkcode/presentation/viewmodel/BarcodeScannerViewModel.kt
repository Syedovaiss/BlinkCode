package com.ovais.blinkcode.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.ovais.blinkcode.data.model.BarcodeResult
import com.ovais.blinkcode.domain.GetConfigurationUseCase
import com.ovais.blinkcode.domain.HasCameraPermissionUseCase
import com.ovais.blinkcode.domain.ScanFromBitmapUseCase
import com.ovais.blinkcode.domain.ScanFromImageUseCase
import com.ovais.blinkcode.domain.ScanFromUriInput
import com.ovais.blinkcode.domain.ScanFromUriUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class BarcodeScannerViewModel(
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase,
    private val scanFromImageUseCase: ScanFromImageUseCase,
    private val scanFromBitmapUseCase: ScanFromBitmapUseCase,
    private val scanFromUriUseCase: ScanFromUriUseCase,
    private val configurationUseCase: GetConfigurationUseCase
) : ViewModel() {

    private val _scanIntervalMillis by lazy {
        MutableStateFlow(configurationUseCase().scanIntervalMillis)
    }
    val scanIntervalMillis: StateFlow<Long>
        get() = _scanIntervalMillis.asStateFlow()

    private val _hasCameraPermissions by lazy {
        MutableStateFlow(hasCameraPermissionUseCase())
    }
    val hasCameraPermissions: StateFlow<Boolean>
        get() = _hasCameraPermissions.asStateFlow()


    var scanState by mutableStateOf<BarcodeResult?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<Throwable?>(null)
        private set

    /**
     * Scans barcode from InputImage
     */
    fun scanFromImage(inputImage: InputImage) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val result = scanFromImageUseCase(inputImage)
                result.fold(
                    onSuccess = { barcodes ->
                        scanState = BarcodeResult.success(barcodes)
                    },
                    onFailure = { throwable ->
                        error = throwable
                        scanState = BarcodeResult.failure(throwable)
                        Timber.e(throwable, "Failed to scan from image")
                    }
                )
            } catch (e: Exception) {
                error = e
                scanState = BarcodeResult.failure(e)
                Timber.e(e, "Error scanning from image")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Scans barcode from Bitmap
     */
    fun scanFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val result = scanFromBitmapUseCase(bitmap)
                result.fold(
                    onSuccess = { barcodes ->
                        scanState = BarcodeResult.success(barcodes)
                    },
                    onFailure = { throwable ->
                        error = throwable
                        scanState = BarcodeResult.failure(throwable)
                        Timber.e(throwable, "Failed to scan from bitmap")
                    }
                )
            } catch (e: Exception) {
                error = e
                scanState = BarcodeResult.failure(e)
                Timber.e(e, "Error scanning from bitmap")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Scans barcode from Uri
     */
    fun scanFromUri(uri: Uri, context: android.content.Context) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val result = scanFromUriUseCase(ScanFromUriInput(uri, context))
                result.fold(
                    onSuccess = { barcodes ->
                        scanState = BarcodeResult.success(barcodes)
                    },
                    onFailure = { throwable ->
                        error = throwable
                        scanState = BarcodeResult.failure(throwable)
                        Timber.e(throwable, "Failed to scan from URI")
                    }
                )
            } catch (e: Exception) {
                error = e
                scanState = BarcodeResult.failure(e)
                Timber.e(e, "Error scanning from URI")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Clears the current scan state
     */
    fun clearScanState() {
        scanState = null
        error = null
    }
}


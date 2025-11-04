package com.ovais.blinkcode.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.mlkit.vision.common.InputImage
import com.ovais.blinkcode.core.manager.BlinkCodeManager
import com.ovais.blinkcode.core.manager.DefaultBlinkCodeManager
import com.ovais.blinkcode.data.BlinkCodeConfig
import com.ovais.blinkcode.data.Size
import com.ovais.blinkcode.domain.GenerateBarcodeInput
import com.ovais.blinkcode.domain.GenerateQRCodeInput
import com.ovais.blinkcode.domain.ScanFromUriInput
import com.ovais.blinkcode.presentation.viewmodel.BarcodeGeneratorViewModel
import com.ovais.blinkcode.presentation.viewmodel.BarcodeScannerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/**
 * Main entry point for BlinkCode SDK
 * Provides easy-to-use APIs for barcode scanning and generation
 */
object BlinkCode {
    private var manager: BlinkCodeManager? = null

    /**
     * Initialize the BlinkCode SDK
     * Must be called before using any SDK features
     *
     * @param context Application context
     * @param config Optional configuration
     */
    @JvmStatic
    fun initialize(context: Context, config: BlinkCodeConfig = BlinkCodeConfig()) {
        manager = DefaultBlinkCodeManager()
        manager?.initialize(context, config)
    }

    /**
     * Get BarcodeScannerViewModel instance
     * Requires SDK to be initialized
     */
    @JvmStatic
    fun getBarcodeScannerViewModel(): BarcodeScannerViewModel {
        checkInitialized()
        return GlobalContext.get().get()
    }

    /**
     * Get BarcodeGeneratorViewModel instance
     * Requires SDK to be initialized
     */
    @JvmStatic
    fun getBarcodeGeneratorViewModel(): BarcodeGeneratorViewModel {
        checkInitialized()
        return GlobalContext.get().get()
    }

    /**
     * Scan barcode from InputImage
     * This is a suspend function and should be called from a coroutine
     */
    suspend fun scanFromImage(
        inputImage: InputImage,
        onResult: (Result<List<String>>) -> Unit
    ) {
        checkInitialized()
        val viewModel = getBarcodeScannerViewModel()
        viewModel.scanFromImage(inputImage)
        
        // Note: ViewModel is async, so we need to observe the state
        // For a simpler API, you might want to use the repository directly
        CoroutineScope(Dispatchers.IO).launch {
            // Wait for result
        }
    }

    /**
     * Scan barcode from Bitmap
     */
    suspend fun scanFromBitmap(
        bitmap: Bitmap,
        onResult: (Result<List<String>>) -> Unit
    ) {
        checkInitialized()
        val viewModel = getBarcodeScannerViewModel()
        viewModel.scanFromBitmap(bitmap)
    }

    /**
     * Scan barcode from Uri
     */
    suspend fun scanFromUri(
        uri: Uri,
        context: Context,
        onResult: (Result<List<String>>) -> Unit
    ) {
        checkInitialized()
        val viewModel = getBarcodeScannerViewModel()
        viewModel.scanFromUri(uri, context)
    }

    /**
     * Generate QR code
     */
    fun generateQRCode(
        content: String,
        size: Size,
        foregroundColor: Color = Color.Black,
        backgroundColor: Color = Color.White,
        onResult: (Result<Bitmap>) -> Unit
    ) {
        checkInitialized()
        val viewModel = getBarcodeGeneratorViewModel()
        viewModel.generateQRCode(content, size, foregroundColor, backgroundColor)
        // Note: Result will be available in viewModel.generatedBitmap
    }

    /**
     * Generate barcode
     */
    fun generateBarcode(
        content: String,
        format: BarcodeFormat,
        size: Size,
        foregroundColor: Color = Color.Black,
        backgroundColor: Color = Color.White,
        onResult: (Result<Bitmap>) -> Unit
    ) {
        checkInitialized()
        val viewModel = getBarcodeGeneratorViewModel()
        viewModel.generateBarcode(content, format, size, foregroundColor, backgroundColor)
    }

    private fun checkInitialized() {
        if (manager == null) {
            throw IllegalStateException(
                "BlinkCode SDK not initialized. Call BlinkCode.initialize() first."
            )
        }
    }
}


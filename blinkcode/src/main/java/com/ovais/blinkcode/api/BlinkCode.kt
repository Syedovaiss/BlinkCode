package com.ovais.blinkcode.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.ovais.blinkcode.core.manager.BlinkCodeManager
import com.ovais.blinkcode.core.manager.DefaultBlinkCodeManager
import com.ovais.blinkcode.data.BlinkCodeConfig
import com.ovais.blinkcode.data.Size
import com.ovais.blinkcode.domain.GenerateBarcodeInput
import com.ovais.blinkcode.domain.GenerateBarcodeUseCase
import com.ovais.blinkcode.domain.GenerateQRCodeInput
import com.ovais.blinkcode.domain.GenerateQRCodeUseCase
import com.ovais.blinkcode.domain.ScanFromBitmapUseCase
import com.ovais.blinkcode.domain.ScanFromImageUseCase
import com.ovais.blinkcode.domain.ScanFromUriInput
import com.ovais.blinkcode.domain.ScanFromUriUseCase
import com.ovais.blinkcode.presentation.BlinkCodeBarcodeView
import com.ovais.blinkcode.presentation.BlinkCodeCameraView
import org.koin.core.context.GlobalContext

/**
 * Main entry point for BlinkCode SDK
 * Provides easy-to-use APIs for barcode scanning and generation
 *
 * This is the single entry point for all SDK functionality.
 * Use this object to:
 * - Initialize the SDK
 * - Scan barcodes from various sources
 * - Generate QR codes and barcodes
 * - Create composable views for scanning and generation
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
     * Scan barcode from InputImage
     *
     * @param inputImage The InputImage to scan
     * @return Result containing list of barcode values or error
     */
    suspend fun scanFromImage(inputImage: InputImage): Result<List<String>> {
        checkInitialized()
        val useCase: ScanFromImageUseCase = GlobalContext.get().get()
        return useCase(inputImage)
    }

    /**
     * Scan barcode from Bitmap
     *
     * @param bitmap The Bitmap to scan
     * @return Result containing list of barcode values or error
     */
    suspend fun scanFromBitmap(bitmap: Bitmap): Result<List<String>> {
        checkInitialized()
        val useCase: ScanFromBitmapUseCase = GlobalContext.get().get()
        return useCase(bitmap)
    }

    /**
     * Scan barcode from Uri
     *
     * @param uri The Uri of the image to scan
     * @param context The context to use for loading the image
     * @return Result containing list of barcode values or error
     */
    suspend fun scanFromUri(uri: Uri, context: Context): Result<List<String>> {
        checkInitialized()
        val useCase: ScanFromUriUseCase = GlobalContext.get().get()
        return useCase(ScanFromUriInput(uri, context))
    }

    /**
     * Generate QR code
     *
     * @param content The content to encode in the QR code
     * @param size The size of the QR code
     * @param foregroundColor The foreground color (default: Black)
     * @param backgroundColor The background color (default: White)
     * @return Result containing the generated Bitmap or error
     */
    suspend fun generateQRCode(
        content: String,
        size: Size,
        foregroundColor: Color = Color.Black,
        backgroundColor: Color = Color.White
    ): Result<Bitmap> {
        checkInitialized()
        val useCase: GenerateQRCodeUseCase = GlobalContext.get().get()
        return useCase(GenerateQRCodeInput(content, size, foregroundColor, backgroundColor))
    }

    /**
     * Generate barcode
     *
     * @param content The content to encode in the barcode
     * @param format The barcode format
     * @param size The size of the barcode
     * @param foregroundColor The foreground color (default: Black)
     * @param backgroundColor The background color (default: White)
     * @return Result containing the generated Bitmap or error
     */
    suspend fun generateBarcode(
        content: String,
        format: BarcodeFormat,
        size: Size,
        foregroundColor: Color = Color.Black,
        backgroundColor: Color = Color.White
    ): Result<Bitmap> {
        checkInitialized()
        val useCase: GenerateBarcodeUseCase = GlobalContext.get().get()
        return useCase(
            GenerateBarcodeInput(
                content,
                format,
                size,
                foregroundColor,
                backgroundColor
            )
        )
    }

    /**
     * Create a camera view for barcode scanning
     * This is a composable factory function that creates a ready-to-use camera view
     *
     * @param onBarcodeDetected Callback invoked when a barcode is detected
     * @param onError Optional callback invoked when an error occurs
     * @return Composable camera view
     */
    @Composable
    fun CreateCameraView(
        onBarcodeDetected: (String) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ) {
        checkInitialized()
        BlinkCodeCameraView(
            onBarcodeDetected = onBarcodeDetected,
            onError = onError
        )
    }

    /**
     * Create a view for displaying a generated barcode or QR code
     * This is a composable factory function that displays a bitmap
     *
     * @param bitmap The bitmap to display (can be null)
     * @param modifier Modifier for styling
     * @return Composable barcode view
     */
    @Composable
    fun CreateBarcodeView(
        bitmap: Bitmap?,
        modifier: Modifier = Modifier
    ) {
        checkInitialized()
        BlinkCodeBarcodeView(
            bitmap = bitmap,
            modifier = modifier
        )
    }

    private fun checkInitialized() {
        if (manager == null) {
            throw IllegalStateException(
                "BlinkCode SDK not initialized. Call BlinkCode.initialize() first."
            )
        }
    }
}


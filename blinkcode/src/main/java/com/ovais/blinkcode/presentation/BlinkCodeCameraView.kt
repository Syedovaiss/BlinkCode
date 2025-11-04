package com.ovais.blinkcode.presentation

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.ovais.blinkcode.core.exceptions.CameraPermissionNotGrantedException
import com.ovais.blinkcode.presentation.viewmodel.BarcodeScannerViewModel
import com.ovais.blinkcode.utils.throttle.Throttle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Composable camera view with ML Kit barcode scanning
 * Throws CameraPermissionNotGrantedException if camera permission is not granted
 *
 * @param onBarcodeDetected Callback invoked when a barcode is detected
 * @param onError Callback invoked when an error occurs (optional)
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun BlinkCodeCameraView(
    onBarcodeDetected: (String) -> Unit,
    onError: ((Throwable) -> Unit)? = null
) {
    val viewModel: BarcodeScannerViewModel = koinViewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasCameraPermission by viewModel.hasCameraPermissions.collectAsStateWithLifecycle()
    val scanIntervalMillis by viewModel.scanIntervalMillis.collectAsStateWithLifecycle()

    val throttle = remember { Throttle(scanIntervalMillis, TimeUnit.MILLISECONDS) }

    var permissionChecked by remember { mutableStateOf(false) }
    var permissionError by remember { mutableStateOf<Throwable?>(null) }

    // Check camera permission
    LaunchedEffect(hasCameraPermission) {
        try {
            if (hasCameraPermission.not()) {
                val error = CameraPermissionNotGrantedException()
                permissionError = error
                onError?.invoke(error)
            } else {
                permissionChecked = true
            }
        } catch (e: Exception) {
            permissionError = e
            onError?.invoke(e)
            Timber.e(e, "Error checking camera permission")
        }
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var analyzer by remember { mutableStateOf<ImageAnalysis?>(null) }
    var isCameraBound by remember { mutableStateOf(false) }

    // Setup camera provider when permission is granted
    LaunchedEffect(permissionChecked, permissionError) {
        if (permissionChecked && permissionError == null && cameraProvider == null) {
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
            } catch (e: Exception) {
                Timber.e(e, "Error getting camera provider")
                onError?.invoke(e)
            }
        }
    }

    // Cleanup camera when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            try {
                if (isCameraBound) {
                    analyzer?.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    analyzer = null
                    isCameraBound = false
                    Timber.d("Camera resources cleaned up")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error cleaning up camera resources")
            }
        }
    }

    if (permissionChecked && permissionError == null && cameraProvider != null) {
        AndroidView(
            factory = { ctx ->
                val pv = PreviewView(ctx)
                val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

                val provider = cameraProvider ?: return@AndroidView pv

                // Only bind if not already bound
                if (!isCameraBound) {
                    try {
                        // Unbind all existing use cases first to prevent conflicts
                        provider.unbindAll()

                        val preview = Preview.Builder().build().apply {
                            surfaceProvider = pv.surfaceProvider
                        }

                        val barcodeScanner = BarcodeScanning.getClient()
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analyzer = it }

                        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val currentTime = System.currentTimeMillis()

                                // Throttle scanning to prevent too frequent scans
                                if (throttle.shouldExecute(currentTime)) {
                                    val inputImage = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )

                                    barcodeScanner.process(inputImage)
                                        .addOnSuccessListener { barcodes ->
                                            coroutineScope.launch {
                                                barcodes.forEach { barcode ->
                                                    barcode.rawValue?.let { value ->
                                                        try {
                                                            onBarcodeDetected(value)
                                                        } catch (e: Exception) {
                                                            Timber.e(
                                                                e,
                                                                "Error in onBarcodeDetected callback"
                                                            )
                                                            onError?.invoke(e)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Timber.e(exception, "Error scanning barcode")
                                            onError?.invoke(exception)
                                            imageProxy.close()
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    // Skip this frame due to throttling
                                    imageProxy.close()
                                }
                            } else {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        isCameraBound = true
                        Timber.d("Camera bound successfully")
                    } catch (e: Exception) {
                        Timber.e(e, "Error setting up camera")
                        onError?.invoke(e)
                    }
                }

                pv
            },
            onRelease = {
                // Cleanup when view is released
                try {
                    if (isCameraBound) {
                        analyzer?.clearAnalyzer()
                        cameraProvider?.unbindAll()
                        analyzer = null
                        isCameraBound = false
                        Timber.d("Camera view released")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error releasing camera view")
                }
            }
        )
    }
}
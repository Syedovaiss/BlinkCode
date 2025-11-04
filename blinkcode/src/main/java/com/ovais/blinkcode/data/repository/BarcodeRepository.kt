package com.ovais.blinkcode.data.repository

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.ovais.blinkcode.core.exceptions.BarcodeScanningException
import com.ovais.blinkcode.core.exceptions.ImageLoadException
import com.ovais.blinkcode.core.exceptions.InvalidInputException
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.resumeWithException

interface BarcodeRepository {
    suspend fun scanFromImage(inputImage: InputImage): Result<List<String>>
    suspend fun scanFromBitmap(bitmap: Bitmap): Result<List<String>>
    suspend fun scanFromUri(uri: Uri, context: android.content.Context): Result<List<String>>
}

class DefaultBarcodeRepository : BarcodeRepository {

    private val barcodeScanner = BarcodeScanning.getClient()

    override suspend fun scanFromImage(inputImage: InputImage): Result<List<String>> {
        return try {
            val result =
                kotlinx.coroutines.suspendCancellableCoroutine<List<Barcode>> { continuation ->
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            continuation.resume(barcodes) { }
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(
                                BarcodeScanningException(
                                    "Failed to scan barcode from image",
                                    exception
                                )
                            )
                        }
                }

            val barcodeValues = result.mapNotNull { it.rawValue }
            if (barcodeValues.isEmpty()) {
                Result.failure(
                    BarcodeScanningException("No barcode found in image")
                )
            } else {
                Result.success(barcodeValues)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error scanning barcode from image")
            Result.failure(
                e as? BarcodeScanningException
                    ?: BarcodeScanningException("Failed to scan barcode from image", e)
            )
        }
    }

    override suspend fun scanFromBitmap(bitmap: Bitmap): Result<List<String>> {
        return try {
            if (bitmap.isRecycled) {
                return Result.failure(
                    InvalidInputException("Bitmap is recycled")
                )
            }

            val inputImage = InputImage.fromBitmap(bitmap, 0)
            scanFromImage(inputImage)
        } catch (e: Exception) {
            Timber.e(e, "Error scanning barcode from bitmap")
            Result.failure(
                BarcodeScanningException("Failed to scan barcode from bitmap", e)
            )
        }
    }

    override suspend fun scanFromUri(
        uri: Uri,
        context: android.content.Context
    ): Result<List<String>> {
        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    ?: return Result.failure(
                        ImageLoadException("Failed to decode image from URI")
                    )
            }

            scanFromBitmap(bitmap)
        } catch (e: IOException) {
            Timber.e(e, "Error loading image from URI")
            Result.failure(
                ImageLoadException("Failed to load image from URI: ${e.message}", e)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error scanning barcode from URI")
            Result.failure(
                BarcodeScanningException("Failed to scan barcode from URI", e)
            )
        }
    }
}


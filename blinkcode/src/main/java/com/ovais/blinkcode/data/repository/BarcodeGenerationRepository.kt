package com.ovais.blinkcode.data.repository

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.ovais.blinkcode.core.exceptions.InvalidInputException
import com.ovais.blinkcode.core.exceptions.QRCodeGenerationException
import com.ovais.blinkcode.data.Size
import timber.log.Timber
import java.util.Hashtable

interface BarcodeGenerationRepository {
    suspend fun generateQRCode(
        content: String,
        size: Size,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Result<Bitmap>

    suspend fun generateBarcode(
        content: String,
        format: BarcodeFormat,
        size: Size,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Result<Bitmap>
}

class DefaultBarcodeGenerationRepository : BarcodeGenerationRepository {

    override suspend fun generateQRCode(
        content: String,
        size: Size,
        foregroundColor: Int,
        backgroundColor: Int
    ): Result<Bitmap> {
        return generateBarcode(
            content = content,
            format = BarcodeFormat.QR_CODE,
            size = size,
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor
        )
    }

    override suspend fun generateBarcode(
        content: String,
        format: BarcodeFormat,
        size: Size,
        foregroundColor: Int,
        backgroundColor: Int
    ): Result<Bitmap> {
        return try {
            if (content.isBlank()) {
                return Result.failure(
                    InvalidInputException("Content cannot be empty")
                )
            }

            if (size.width <= 0 || size.height <= 0) {
                return Result.failure(
                    InvalidInputException("Size width and height must be greater than 0")
                )
            }

            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1

            val writer = MultiFormatWriter()
            val bitMatrix = try {
                writer.encode(content, format, size.width, size.height, hints)
            } catch (e: WriterException) {
                return Result.failure(
                    QRCodeGenerationException("Failed to encode barcode: ${e.message}", e)
                )
            }

            val bitmap = createBitmap(size.width, size.height, Bitmap.Config.RGB_565)
            for (x in 0 until size.width) {
                for (y in 0 until size.height) {
                    bitmap[x, y] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
                }
            }

            Result.success(bitmap)
        } catch (e: Exception) {
            Timber.e(e, "Error generating barcode")
            Result.failure(
                QRCodeGenerationException("Failed to generate barcode: ${e.message}", e)
            )
        }
    }
}


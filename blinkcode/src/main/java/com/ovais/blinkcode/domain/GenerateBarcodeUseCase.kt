package com.ovais.blinkcode.domain

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.ovais.blinkcode.data.Size
import com.ovais.blinkcode.data.repository.BarcodeGenerationRepository
import com.ovais.blinkcode.utils.usecase.ParameterizedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

data class GenerateBarcodeInput(
    val content: String,
    val format: BarcodeFormat,
    val size: Size,
    val foregroundColor: Color = Color.Black,
    val backgroundColor: Color = Color.White
)

fun interface GenerateBarcodeUseCase : ParameterizedUseCase<GenerateBarcodeInput, Result<Bitmap>>

class DefaultGenerateBarcodeUseCase(
    private val barcodeGenerationRepository: BarcodeGenerationRepository,
    private val dispatcherIO: CoroutineDispatcher
) : GenerateBarcodeUseCase {

    override suspend fun invoke(input: GenerateBarcodeInput): Result<Bitmap> {
        return withContext(dispatcherIO) {
            barcodeGenerationRepository.generateBarcode(
                content = input.content,
                format = input.format,
                size = input.size,
                foregroundColor = input.foregroundColor.toArgb(),
                backgroundColor = input.backgroundColor.toArgb()
            )
        }
    }
}


package com.ovais.blinkcode.domain

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ovais.blinkcode.data.Size
import com.ovais.blinkcode.data.repository.BarcodeGenerationRepository
import com.ovais.blinkcode.utils.usecase.ParameterizedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

data class GenerateQRCodeInput(
    val content: String,
    val size: Size,
    val foregroundColor: Color = Color.Black,
    val backgroundColor: Color = Color.White
)

fun interface GenerateQRCodeUseCase : ParameterizedUseCase<GenerateQRCodeInput, Result<Bitmap>>

class DefaultGenerateQRCodeUseCase(
    private val barcodeGenerationRepository: BarcodeGenerationRepository,
    private val dispatcherIO: CoroutineDispatcher
) : GenerateQRCodeUseCase {
    override suspend fun invoke(input: GenerateQRCodeInput): Result<Bitmap> {
        return withContext(dispatcherIO) {
            barcodeGenerationRepository.generateQRCode(
                content = input.content,
                size = input.size,
                foregroundColor = input.foregroundColor.toArgb(),
                backgroundColor = input.backgroundColor.toArgb()
            )
        }
    }
}


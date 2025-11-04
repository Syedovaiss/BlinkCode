package com.ovais.blinkcode.domain

import android.graphics.Bitmap
import com.ovais.blinkcode.data.repository.BarcodeRepository
import com.ovais.blinkcode.utils.usecase.ParameterizedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface ScanFromBitmapUseCase : ParameterizedUseCase<Bitmap, Result<List<String>>>

class DefaultScanFromBitmapUseCase(
    private val barcodeRepository: BarcodeRepository,
    private val dispatcherIO: CoroutineDispatcher
) : ScanFromBitmapUseCase {
    override suspend fun invoke(input: Bitmap): Result<List<String>> {
        return withContext(dispatcherIO) {
            barcodeRepository.scanFromBitmap(input)
        }
    }
}


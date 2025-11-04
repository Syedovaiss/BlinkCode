package com.ovais.blinkcode.domain

import com.google.mlkit.vision.common.InputImage
import com.ovais.blinkcode.data.repository.BarcodeRepository
import com.ovais.blinkcode.utils.usecase.ParameterizedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface ScanFromImageUseCase : ParameterizedUseCase<InputImage, Result<List<String>>>

class DefaultScanFromImageUseCase(
    private val barcodeRepository: BarcodeRepository,
    private val dispatcherIO: CoroutineDispatcher
) : ScanFromImageUseCase {
    override suspend fun invoke(input: InputImage): Result<List<String>> {
        return withContext(dispatcherIO) { barcodeRepository.scanFromImage(input) }
    }
}


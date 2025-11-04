package com.ovais.blinkcode.domain

import android.content.Context
import android.net.Uri
import com.ovais.blinkcode.core.exceptions.InvalidInputException
import com.ovais.blinkcode.data.repository.BarcodeRepository
import com.ovais.blinkcode.utils.usecase.ParameterizedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

data class ScanFromUriInput(
    val uri: Uri,
    val context: Context
)

fun interface ScanFromUriUseCase : ParameterizedUseCase<ScanFromUriInput, Result<List<String>>>

class DefaultScanFromUriUseCase(
    private val barcodeRepository: BarcodeRepository,
    private val dispatcherIO: CoroutineDispatcher
) : ScanFromUriUseCase {
    override suspend fun invoke(input: ScanFromUriInput): Result<List<String>> {
        if (input.uri == Uri.EMPTY) {
            return Result.failure(
                InvalidInputException("URI cannot be empty")
            )
        }
        return withContext(dispatcherIO) {
            barcodeRepository.scanFromUri(input.uri, input.context)
        }
    }
}


package com.ovais.blinkcode.data.model

/**
 * Represents the result of a barcode scan operation
 */
data class BarcodeResult(
    val barcodes: List<String>,
    val isSuccess: Boolean,
    val error: Throwable? = null
) {
    companion object {
        fun success(barcodes: List<String>): BarcodeResult {
            return BarcodeResult(barcodes = barcodes, isSuccess = true)
        }

        fun failure(error: Throwable): BarcodeResult {
            return BarcodeResult(barcodes = emptyList(), isSuccess = false, error = error)
        }
    }
}


package com.ovais.blinkcode.core.exceptions

/**
 * Base exception for all BlinkCode SDK errors
 */
sealed class BlinkCodeException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Thrown when camera permission is not granted
 */
class CameraPermissionNotGrantedException(
    message: String = "Camera permission is not granted. Please request permission before using camera features."
) : BlinkCodeException(message)

/**
 * Thrown when scanning fails
 */
class BarcodeScanningException(
    message: String = "Failed to scan barcode",
    cause: Throwable? = null
) : BlinkCodeException(message, cause)

/**
 * Thrown when QR code generation fails
 */
class QRCodeGenerationException(
    message: String = "Failed to generate QR code",
    cause: Throwable? = null
) : BlinkCodeException(message, cause)

/**
 * Thrown when invalid input is provided
 */
class InvalidInputException(
    message: String = "Invalid input provided"
) : BlinkCodeException(message)

/**
 * Thrown when image loading fails
 */
class ImageLoadException(
    message: String = "Failed to load image",
    cause: Throwable? = null
) : BlinkCodeException(message, cause)


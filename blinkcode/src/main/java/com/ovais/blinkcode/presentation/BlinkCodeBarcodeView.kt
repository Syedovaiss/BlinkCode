package com.ovais.blinkcode.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * Composable view for displaying a generated barcode or QR code
 * 
 * @param bitmap The bitmap to display
 * @param modifier Modifier for styling
 * @param contentScale How to scale the image (default: Fit)
 */
@Composable
fun BlinkCodeBarcodeView(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Generated barcode",
                modifier = Modifier.size(it.width.dp, it.height.dp),
                contentScale = contentScale
            )
        }
    }
}


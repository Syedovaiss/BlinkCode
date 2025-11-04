package com.ovais.blinkcode

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ovais.blinkcode.presentation.BlinkCodeCameraView
import com.ovais.blinkcode.ui.theme.BlinkcodeTheme

class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlinkcodeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BlinkCodeCameraView(
                        onBarcodeDetected = {
                            showToast(it)
                        },
                        onError = {
                            Log.e("error---", it.stackTraceToString())
                            showToast(it.stackTraceToString())
                        }
                    )
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

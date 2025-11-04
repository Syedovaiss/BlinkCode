package com.ovais.blinkcode

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.ovais.blinkcode.api.BlinkCode
import com.ovais.blinkcode.data.Size
import com.ovais.blinkcode.ui.theme.BlinkcodeTheme
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap

class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlinkcodeTheme {
                BlinkCodeDemoApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlinkCodeDemoApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BlinkCode SDK Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Scan") },
                    label = { Text("Scan") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
            NavigationBarItem(
                icon = { Icon(Icons.Default.QrCode, contentDescription = "Generate") },
                label = { Text("Generate") },
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
                    label = { Text("Info") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> ScanScreen()
                1 -> GenerateScreen()
                2 -> InfoScreen()
            }
        }
    }
}

@Composable
fun ScanScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var scanResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedScanType by remember { mutableIntStateOf(0) } // 0: Camera, 1: Bitmap, 2: URI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Barcode Scanning",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Scan type selector
        Text(
            text = "Select Scan Method:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedScanType == 0,
                onClick = { selectedScanType = 0 },
                label = { Text("Camera") }
            )
            FilterChip(
                selected = selectedScanType == 1,
                onClick = { selectedScanType = 1 },
                label = { Text("Bitmap") }
            )
            FilterChip(
                selected = selectedScanType == 2,
                onClick = { selectedScanType = 2 },
                label = { Text("URI") }
            )
        }

        when (selectedScanType) {
            0 -> {
                // Camera scanning
                Text(
                    text = "Camera Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(bottom = 16.dp)
                ) {
                    BlinkCode.CreateCameraView(
                        onBarcodeDetected = { barcode ->
                            scanResult = "Scanned: $barcode"
                            showToast(context, scanResult ?: "")
                        },
                        onError = { error ->
                            scanResult = "Error: ${error.message}"
                            showToast(context, scanResult ?: "")
                        }
                    )
                }
            }
            1 -> {
                // Bitmap scanning
                Text(
                    text = "Bitmap Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            scanResult = null
                            try {
                                // Create a simple test bitmap with QR code
                                // In real app, you'd pick from gallery or camera
                                val testBitmap = createTestBitmap(context)
                                val result = BlinkCode.scanFromBitmap(testBitmap)
                                result.fold(
                                    onSuccess = { barcodes ->
                                        scanResult = if (barcodes.isNotEmpty()) {
                                            "Found ${barcodes.size} barcode(s):\n${barcodes.joinToString("\n")}"
                                        } else {
                                            "No barcodes found"
                                        }
                                    },
                                    onFailure = { error ->
                                        scanResult = "Error: ${error.message}"
                                    }
                                )
                            } catch (e: Exception) {
                                scanResult = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Scan Test Bitmap")
                }
            }
            2 -> {
                // URI scanning
                Text(
                    text = "URI Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                val imagePicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        scope.launch {
                            isLoading = true
                            scanResult = null
                            try {
                                val result = BlinkCode.scanFromUri(it, context)
                                result.fold(
                                    onSuccess = { barcodes ->
                                        scanResult = if (barcodes.isNotEmpty()) {
                                            "Found ${barcodes.size} barcode(s):\n${barcodes.joinToString("\n")}"
                                        } else {
                                            "No barcodes found"
                                        }
                                    },
                                    onFailure = { error ->
                                        scanResult = "Error: ${error.message}"
                                    }
                                )
                            } catch (e: Exception) {
                                scanResult = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
                
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Pick Image from Gallery")
                }
            }
        }

        // Display scan result
        scanResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Scan Result:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun GenerateScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedType by remember { mutableIntStateOf(0) } // 0: QR Code, 1: Barcode
    var content by remember { mutableStateOf("Hello World") }
    var selectedFormat by remember { mutableStateOf(BarcodeFormat.CODE_128) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Barcode Generation",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Type selector
        Text(
            text = "Select Type:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == 0,
                onClick = { selectedType = 0 },
                label = { Text("QR Code") }
            )
            FilterChip(
                selected = selectedType == 1,
                onClick = { selectedType = 1 },
                label = { Text("Barcode") }
            )
        }

        // Content input
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content to Encode") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Barcode format selector (only for barcode type)
        if (selectedType == 1) {
            Text(
                text = "Barcode Format:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val formats = listOf(
                BarcodeFormat.CODE_128,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODE_39
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formats.forEach { format ->
                    FilterChip(
                        selected = selectedFormat == format,
                        onClick = { selectedFormat = format },
                        label = { 
                            Text(
                                format.toString().split("_").lastOrNull() ?: format.toString()
                            ) 
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Generate button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    generatedBitmap = null
                    try {
                        val result = if (selectedType == 0) {
                            BlinkCode.generateQRCode(
                                content = content,
                                size = Size(400, 400)
                            )
                        } else {
                            BlinkCode.generateBarcode(
                                content = content,
                                format = selectedFormat,
                                size = Size(400, 200)
                            )
                        }
                        
                        result.fold(
                            onSuccess = { bitmap ->
                                generatedBitmap = bitmap
                                showToast(context, "Generated successfully!")
                            },
                            onFailure = { error ->
                                showToast(context, "Error: ${error.message}")
                            }
                        )
                    } catch (e: Exception) {
                        showToast(context, "Error: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && content.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Generate ${if (selectedType == 0) "QR Code" else "Barcode"}")
        }

        // Display generated barcode
        generatedBitmap?.let { bitmap ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Generated ${if (selectedType == 0) "QR Code" else "Barcode"}:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    BlinkCode.CreateBarcodeView(
                        bitmap = bitmap,
                        modifier = Modifier
                            .size(bitmap.width.dp, bitmap.height.dp)
                            .padding(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Content: $content",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "BlinkCode SDK",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Available Features:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                FeatureItem(
                    icon = Icons.Default.CameraAlt,
                    title = "Camera Scanning",
                    description = "Real-time barcode scanning using device camera"
                )
                
                FeatureItem(
                    icon = Icons.Default.Image,
                    title = "Bitmap Scanning",
                    description = "Scan barcodes from Bitmap images"
                )
                
                FeatureItem(
                    icon = Icons.Default.Link,
                    title = "URI Scanning",
                    description = "Scan barcodes from image URIs"
                )
                
                FeatureItem(
                    icon = Icons.Default.QrCode,
                    title = "QR Code Generation",
                    description = "Generate QR codes with custom content and colors"
                )
                
                FeatureItem(
                    icon = Icons.Default.ViewModule,
                    title = "Barcode Generation",
                    description = "Generate various barcode formats (CODE_128, EAN_13, etc.)"
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "SDK Usage:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = """
1. Initialize SDK in Application:
   BlinkCode.initialize(context)

2. Scan from Camera:
   BlinkCode.createCameraView(onBarcodeDetected = { ... })

3. Scan from Bitmap:
   val result = BlinkCode.scanFromBitmap(bitmap)

4. Scan from URI:
   val result = BlinkCode.scanFromUri(uri, context)

5. Generate QR Code:
   val result = BlinkCode.generateQRCode(content, size)

6. Generate Barcode:
   val result = BlinkCode.generateBarcode(content, format, size)

7. Display Barcode:
   BlinkCode.createBarcodeView(bitmap)
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Helper function to create a test bitmap (for demo purposes)
// Note: This creates a simple colored bitmap for testing
// In a real app, you'd load an actual image with a barcode
suspend fun createTestBitmap(context: Context): Bitmap {
    // Generate a simple QR code for testing
    val result = BlinkCode.generateQRCode(
        content = "Test QR Code for Bitmap Scanning",
        size = Size(200, 200)
    )
    return result.getOrElse {
        // Fallback: create a simple colored bitmap
        createBitmap(200, 200, Bitmap.Config.RGB_565).apply {
            eraseColor(android.graphics.Color.WHITE)
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

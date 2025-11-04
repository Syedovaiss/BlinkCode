# BlinkCode SDK

[![JitPack](https://jitpack.io/v/Syedovaiss/BlinkCode.svg)](https://jitpack.io/#Syedovaiss/BlinkCode)

A powerful, easy-to-use Android SDK for barcode scanning and generation. Built with Kotlin and Jetpack Compose, BlinkCode provides a single entry point for all your barcode needs.

## 📦 Installation

**Via JitPack:**

```kotlin
dependencies {
    implementation("com.github.Syedovaiss:BlinkCode:1.0")
}
```

## Features

- ✅ **Real-time Camera Scanning** - Scan barcodes using device camera with live preview
- ✅ **Image Scanning** - Scan barcodes from Bitmap images or URIs
- ✅ **QR Code Generation** - Generate QR codes with custom content and styling
- ✅ **Barcode Generation** - Support for multiple barcode formats (CODE_128, EAN_13, EAN_8, UPC_A, CODE_39, etc.)
- ✅ **Jetpack Compose Ready** - Built-in composable views for seamless integration
- ✅ **Clean Architecture** - Well-structured, testable, and maintainable codebase
- ✅ **Minimal Dependencies** - Single entry point API for easy integration

## Requirements

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Kotlin**: 2.0.21+
- **Java**: 11+
- **Jetpack Compose**: Required for UI components

## Installation
### Setup JitPack Repository

Add JitPack to your root `settings.gradle.kts`:

### Add to your project
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency in your `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Syedovaiss:BlinkCode:1.0")
}
```

### Using Local Module (Development)

Alternatively, if you're using the module locally:

Add the BlinkCode module to your `settings.gradle.kts`:

```kotlin
include(":blinkcode")
```

In your `app/build.gradle.kts`, add the dependency:

```kotlin
dependencies {
    implementation(project(":blinkcode"))
}
```

### Permissions

Add the required permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

For Android 13+ (API 33+), also add:

```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

For older versions, add:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## Quick Start

### 1. Initialize the SDK

Initialize BlinkCode in your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BlinkCode.initialize(this)
    }
}
```

Or with custom configuration:

```kotlin
BlinkCode.initialize(
    context = this,
    config = BlinkCodeConfig(
        scanningEnabled = true,
        creationEnabled = true,
        loggingEnabled = true,
        scanIntervalMillis = 500,
        qrSize = Size(400, 400)
    )
)
```

### 2. Use in Your App

#### Camera Scanning

```kotlin
@Composable
fun ScanScreen() {
    BlinkCode.CreateCameraView(
        onBarcodeDetected = { barcode ->
            // Handle detected barcode
            println("Detected: $barcode")
        },
        onError = { error ->
            // Handle error
            println("Error: ${error.message}")
        }
    )
}
```

#### Generate QR Code

```kotlin
@Composable
fun GenerateScreen() {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(Unit) {
        val result = BlinkCode.generateQRCode(
            content = "https://example.com",
            size = Size(400, 400),
            foregroundColor = Color.Black,
            backgroundColor = Color.White
        )
        
        result.onSuccess { bitmap ->
            qrBitmap = bitmap
        }.onFailure { error ->
            println("Error: ${error.message}")
        }
    }
    
    BlinkCode.CreateBarcodeView(bitmap = qrBitmap)
}
```

## API Reference

### Initialization

#### `BlinkCode.initialize(context, config)`

Initialize the SDK. Must be called before using any SDK features.

**Parameters:**
- `context: Context` - Application context
- `config: BlinkCodeConfig` - Optional configuration (default: `BlinkCodeConfig()`)

**Example:**
```kotlin
BlinkCode.initialize(context = this)
```

### Scanning

#### `scanFromImage(inputImage: InputImage): Result<List<String>>`

Scan barcode from an ML Kit InputImage.

**Parameters:**
- `inputImage: InputImage` - The InputImage to scan

**Returns:** `Result<List<String>>` - List of detected barcode values

**Example:**
```kotlin
val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)
val result = BlinkCode.scanFromImage(inputImage)
result.onSuccess { barcodes ->
    println("Found ${barcodes.size} barcode(s)")
}
```

#### `scanFromBitmap(bitmap: Bitmap): Result<List<String>>`

Scan barcode from a Bitmap image.

**Parameters:**
- `bitmap: Bitmap` - The Bitmap to scan

**Returns:** `Result<List<String>>` - List of detected barcode values

**Example:**
```kotlin
val result = BlinkCode.scanFromBitmap(bitmap)
result.fold(
    onSuccess = { barcodes -> println("Found: $barcodes") },
    onFailure = { error -> println("Error: ${error.message}") }
)
```

#### `scanFromUri(uri: Uri, context: Context): Result<List<String>>`

Scan barcode from an image URI.

**Parameters:**
- `uri: Uri` - The URI of the image
- `context: Context` - Context to load the image

**Returns:** `Result<List<String>>` - List of detected barcode values

**Example:**
```kotlin
val uri = // ... get URI from image picker
val result = BlinkCode.scanFromUri(uri, context)
result.onSuccess { barcodes ->
    // Handle detected barcodes
}
```

### Generation

#### `generateQRCode(content, size, foregroundColor, backgroundColor): Result<Bitmap>`

Generate a QR code bitmap.

**Parameters:**
- `content: String` - Content to encode
- `size: Size` - Size of the QR code (width x height)
- `foregroundColor: Color` - Foreground color (default: Black)
- `backgroundColor: Color` - Background color (default: White)

**Returns:** `Result<Bitmap>` - Generated QR code bitmap

**Example:**
```kotlin
val result = BlinkCode.generateQRCode(
    content = "Hello World",
    size = Size(400, 400),
    foregroundColor = Color.Black,
    backgroundColor = Color.White
)

result.onSuccess { bitmap ->
    // Use the bitmap
}
```

#### `generateBarcode(content, format, size, foregroundColor, backgroundColor): Result<Bitmap>`

Generate a barcode bitmap.

**Parameters:**
- `content: String` - Content to encode
- `format: BarcodeFormat` - Barcode format (CODE_128, EAN_13, EAN_8, UPC_A, CODE_39, etc.)
- `size: Size` - Size of the barcode (width x height)
- `foregroundColor: Color` - Foreground color (default: Black)
- `backgroundColor: Color` - Background color (default: White)

**Returns:** `Result<Bitmap>` - Generated barcode bitmap

**Example:**
```kotlin
val result = BlinkCode.generateBarcode(
    content = "123456789012",
    format = BarcodeFormat.CODE_128,
    size = Size(400, 200),
    foregroundColor = Color.Black,
    backgroundColor = Color.White
)
```

### UI Components

#### `CreateCameraView(onBarcodeDetected, onError): @Composable`

Create a camera view for real-time barcode scanning.

**Parameters:**
- `onBarcodeDetected: (String) -> Unit` - Callback when barcode is detected
- `onError: ((Throwable) -> Unit)?` - Optional error callback

**Example:**
```kotlin
BlinkCode.CreateCameraView(
    onBarcodeDetected = { barcode ->
        // Handle detected barcode
    },
    onError = { error ->
        // Handle error
    }
)
```

#### `CreateBarcodeView(bitmap, modifier): @Composable`

Create a view to display a generated barcode or QR code.

**Parameters:**
- `bitmap: Bitmap?` - The bitmap to display (can be null)
- `modifier: Modifier` - Optional Compose modifier for styling

**Example:**
```kotlin
BlinkCode.CreateBarcodeView(
    bitmap = qrBitmap,
    modifier = Modifier.size(200.dp)
)
```

## Configuration

### BlinkCodeConfig

Customize SDK behavior with `BlinkCodeConfig`:

```kotlin
data class BlinkCodeConfig(
    val scanningEnabled: Boolean = true,      // Enable scanning features
    val creationEnabled: Boolean = true,       // Enable generation features
    val loggingEnabled: Boolean = true,         // Enable SDK logging
    val qrSize: Size = Size(400, 400),         // Default QR code size
    val defaultColor: Color = Color.Black,    // Default foreground color
    val autoSaveGeneratedFiles: Boolean = false, // Auto-save generated files
    val scanIntervalMillis: Long = 500         // Scan throttling interval
)
```

**Example:**
```kotlin
BlinkCode.initialize(
    context = this,
    config = BlinkCodeConfig(
        loggingEnabled = false,
        scanIntervalMillis = 1000, // Scan every second
        qrSize = Size(500, 500)
    )
)
```

## Supported Barcode Formats

### Scanning
- QR Code
- EAN-13
- EAN-8
- UPC-A
- UPC-E
- Code 128
- Code 39
- Code 93
- Codabar
- ITF
- Data Matrix
- PDF417
- Aztec

### Generation
- QR Code
- CODE_128
- EAN_13
- EAN_8
- UPC_A
- CODE_39
- And more (via ZXing)

## Error Handling

All scanning and generation methods return `Result<T>` which can be handled using Kotlin's Result API:

```kotlin
val result = BlinkCode.scanFromBitmap(bitmap)

// Option 1: Using fold
result.fold(
    onSuccess = { barcodes -> 
        // Handle success
    },
    onFailure = { error -> 
        // Handle error
    }
)

// Option 2: Using onSuccess/onFailure
result.onSuccess { barcodes ->
    // Handle success
}.onFailure { error ->
    // Handle error
}

// Option 3: Using getOrNull/getOrElse
val barcodes = result.getOrNull()
val barcodesOrEmpty = result.getOrElse { emptyList() }
```

## Architecture

BlinkCode SDK follows clean architecture principles:

- **API Layer**: Single entry point (`BlinkCode` object)
- **Domain Layer**: Use cases for business logic
- **Data Layer**: Repositories and data sources
- **Presentation Layer**: UI components and ViewModels

### Dependency Injection

The SDK uses Koin for dependency injection. All dependencies are configured internally and managed automatically.

## Testing

The SDK is designed to be testable. All repositories and use cases use interfaces, making it easy to mock dependencies in tests.

## Proguard/R8 Rules

If you're using ProGuard or R8, add these rules to your `proguard-rules.pro`:

```proguard
# BlinkCode SDK
-keep class com.ovais.blinkcode.** { *; }
-dontwarn com.ovais.blinkcode.**

# Keep ZXing classes
-keep class com.google.zxing.** { *; }

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
```

## Troubleshooting

### Camera Permission Not Granted

Make sure you request camera permission before using the camera view:

```kotlin
val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Camera permission granted
    }
}

// Request permission
launcher.launch(Manifest.permission.CAMERA)
```

### "SDK not initialized" Error

Make sure you call `BlinkCode.initialize()` in your Application class before using any SDK features.

### Camera Binding Errors

The SDK automatically handles camera lifecycle management. If you encounter binding errors, ensure:
- Only one camera view is active at a time
- The camera view is properly disposed when leaving the screen
- Camera permissions are granted

## Sample App

Check out the sample app in the `app` module for complete examples of all SDK features.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues, questions, or feature requests, please open an issue on the repository.

---

**Made with ❤️ using Kotlin and Jetpack Compose**



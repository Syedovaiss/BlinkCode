package com.ovais.blinkcode.core.di

import com.ovais.blinkcode.domain.DefaultGenerateBarcodeUseCase
import com.ovais.blinkcode.domain.DefaultGenerateQRCodeUseCase
import com.ovais.blinkcode.domain.DefaultGetConfigurationUseCase
import com.ovais.blinkcode.domain.DefaultHasCameraPermissionUseCase
import com.ovais.blinkcode.domain.DefaultScanFromBitmapUseCase
import com.ovais.blinkcode.domain.DefaultScanFromImageUseCase
import com.ovais.blinkcode.domain.DefaultScanFromUriUseCase
import com.ovais.blinkcode.domain.GenerateBarcodeUseCase
import com.ovais.blinkcode.domain.GenerateQRCodeUseCase
import com.ovais.blinkcode.domain.GetConfigurationUseCase
import com.ovais.blinkcode.domain.HasCameraPermissionUseCase
import com.ovais.blinkcode.domain.ScanFromBitmapUseCase
import com.ovais.blinkcode.domain.ScanFromImageUseCase
import com.ovais.blinkcode.domain.ScanFromUriUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val factoryModule = module {
    factory<HasCameraPermissionUseCase> {
        DefaultHasCameraPermissionUseCase(get())
    }
    factory<ScanFromImageUseCase> {
        DefaultScanFromImageUseCase(get(), get(named(BACKGROUND)))
    }
    factory<ScanFromBitmapUseCase> {
        DefaultScanFromBitmapUseCase(get(), get(named(BACKGROUND)))
    }
    factory<ScanFromUriUseCase> {
        DefaultScanFromUriUseCase(
            get(),
            get(named(BACKGROUND))
        )
    }
    factory<GenerateQRCodeUseCase> {
        DefaultGenerateQRCodeUseCase(get(), get(named(BACKGROUND)))
    }
    factory<GenerateBarcodeUseCase> {
        DefaultGenerateBarcodeUseCase(
            get(),
            get(named(BACKGROUND))
        )
    }
    factory<GetConfigurationUseCase> { DefaultGetConfigurationUseCase(get()) }
}
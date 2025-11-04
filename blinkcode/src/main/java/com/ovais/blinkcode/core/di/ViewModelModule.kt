package com.ovais.blinkcode.core.di

import com.ovais.blinkcode.presentation.viewmodel.BarcodeScannerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        BarcodeScannerViewModel(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

}
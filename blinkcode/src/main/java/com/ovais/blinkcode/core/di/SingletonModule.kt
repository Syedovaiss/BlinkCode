package com.ovais.blinkcode.core.di

import com.ovais.blinkcode.core.manager.DefaultPermissionManager
import com.ovais.blinkcode.core.manager.PermissionManager
import com.ovais.blinkcode.core.providers.ConfigurationProvider
import com.ovais.blinkcode.core.providers.DefaultConfigurationProvider
import com.ovais.blinkcode.data.repository.BarcodeGenerationRepository
import com.ovais.blinkcode.data.repository.BarcodeRepository
import com.ovais.blinkcode.data.repository.DefaultBarcodeGenerationRepository
import com.ovais.blinkcode.data.repository.DefaultBarcodeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val BACKGROUND = "BACKGROUND"
const val MAIN = "MAIN"
const val DEFAULT = "DEFAULT"

val singletonModule = module {
    single<ConfigurationProvider> {
        DefaultConfigurationProvider()
    }
    single<PermissionManager> {
        DefaultPermissionManager(get())
    }
    single<BarcodeRepository> {
        DefaultBarcodeRepository()
    }
    single<BarcodeGenerationRepository> {
        DefaultBarcodeGenerationRepository()
    }

    single<CoroutineDispatcher>(named(BACKGROUND)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(MAIN)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(DEFAULT)) { Dispatchers.Default }
}
package com.ovais.blinkcode.core.manager

import android.content.Context
import com.ovais.blinkcode.core.di.factoryModule
import com.ovais.blinkcode.core.di.singletonModule
import com.ovais.blinkcode.core.di.viewModelModule
import com.ovais.blinkcode.core.providers.ConfigurationProvider
import com.ovais.blinkcode.data.BlinkCodeConfig
import com.ovais.blinkcode.utils.logger.BlinkCodeLoggingTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

interface BlinkCodeManager {
    fun initialize(
        context: Context,
        config: BlinkCodeConfig = BlinkCodeConfig()
    )
}

class DefaultBlinkCodeManager : BlinkCodeManager {

    private val configurationProvider: ConfigurationProvider by inject(ConfigurationProvider::class.java)
    private var isInitialized = false

    override fun initialize(
        context: Context,
        config: BlinkCodeConfig
    ) {
        if (isInitialized) return
        startKoin {
            androidContext(context)
            androidLogger()
            modules(singletonModule, factoryModule, viewModelModule)
        }
        updateConfiguration(config)
        initLogging(config.loggingEnabled)
        isInitialized = true
    }

    private fun initLogging(isLoggingEnabled: Boolean) {
        if (isLoggingEnabled) {
            Timber.plant(BlinkCodeLoggingTree())
        }
    }

    private fun updateConfiguration(config: BlinkCodeConfig) {
        configurationProvider.updateConfiguration(config)
    }
}
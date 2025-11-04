package com.ovais.blinkcode.core.providers

import com.ovais.blinkcode.data.BlinkCodeConfig

internal interface ConfigurationProvider {
    fun updateConfiguration(config: BlinkCodeConfig)
    fun get(): BlinkCodeConfig
}

internal class DefaultConfigurationProvider : ConfigurationProvider {
    private var config: BlinkCodeConfig? = null

    override fun updateConfiguration(config: BlinkCodeConfig) {
        this.config = config
    }

    override fun get(): BlinkCodeConfig {
        return config ?: BlinkCodeConfig()
    }
}
package com.ovais.blinkcode.domain

import com.ovais.blinkcode.core.providers.ConfigurationProvider
import com.ovais.blinkcode.data.BlinkCodeConfig
import com.ovais.blinkcode.utils.usecase.UseCase

interface GetConfigurationUseCase : UseCase<BlinkCodeConfig>

internal class DefaultGetConfigurationUseCase(
    private val configurationProvider: ConfigurationProvider
) : GetConfigurationUseCase {
    override fun invoke(): BlinkCodeConfig {
        return configurationProvider.get()
    }
}
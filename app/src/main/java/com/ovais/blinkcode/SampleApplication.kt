package com.ovais.blinkcode

import android.app.Application
import com.ovais.blinkcode.core.manager.BlinkCodeManager
import com.ovais.blinkcode.core.manager.DefaultBlinkCodeManager

class SampleApplication : Application() {

    private lateinit var blinkCodeManager: BlinkCodeManager

    override fun onCreate() {
        super.onCreate()
        initializeBlinkCode()
    }

    private fun initializeBlinkCode() {
        blinkCodeManager = DefaultBlinkCodeManager()
        blinkCodeManager.initialize(this)
    }
}
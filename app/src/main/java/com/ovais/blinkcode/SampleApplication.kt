package com.ovais.blinkcode

import android.app.Application
import com.ovais.blinkcode.api.BlinkCode

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BlinkCode.initialize(this)
    }
}
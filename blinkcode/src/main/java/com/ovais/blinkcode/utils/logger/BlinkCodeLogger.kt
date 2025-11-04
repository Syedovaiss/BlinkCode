package com.ovais.blinkcode.utils.logger

import timber.log.Timber


internal class BlinkCodeLoggingTree : Timber.DebugTree() {
    private companion object {
        private const val TAG = "Blink Code Logs =>"
    }

    override fun i(message: String?, vararg args: Any?) {
        super.i("$TAG $message", *args)
    }

    override fun e(message: String?, vararg args: Any?) {
        super.e("$TAG $message", *args)
    }

    override fun e(t: Throwable?) {
        e("$TAG ${t?.stackTraceToString()}")
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
        e("$TAG ${t?.stackTraceToString()}")
    }

}
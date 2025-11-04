package com.ovais.blinkcode.utils.throttle

import java.util.concurrent.TimeUnit

/**
 * Utility class to throttle execution based on time intervals
 */
class Throttle(
    interval: Long,
    timeUnit: TimeUnit
) {
    private var lastExecutionTime = 0L
    private val intervalMillis = timeUnit.toMillis(interval)

    /**
     * Checks if execution should be allowed based on the throttle interval
     * @param currentTime Current time in milliseconds
     * @return true if execution should be allowed, false otherwise
     */
    fun shouldExecute(currentTime: Long = System.currentTimeMillis()): Boolean {
        val timeSinceLastExecution = currentTime - lastExecutionTime
        return if (timeSinceLastExecution >= intervalMillis) {
            lastExecutionTime = currentTime
            true
        } else {
            false
        }
    }

    /**
     * Resets the throttle to allow immediate execution
     */
    fun reset() {
        lastExecutionTime = 0L
    }
}


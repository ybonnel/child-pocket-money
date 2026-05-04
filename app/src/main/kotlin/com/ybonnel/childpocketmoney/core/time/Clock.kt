package com.ybonnel.childpocketmoney.core.time

import kotlinx.datetime.Clock as KotlinClock
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction over kotlinx-datetime Clock for testability.
 */
interface Clock {
    fun now(): Instant
}

/**
 * Real implementation using the system clock.
 */
@Singleton
class SystemClock @Inject constructor() : Clock {
    override fun now(): Instant = KotlinClock.System.now()
}

package dev.toolkt.core.platform

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

expect object PlatformSystem {
    /**
     * Hints the system to start a garbage collection run in a best-effort manner. This utility is meant to help with
     * testing and debugging memory management issues, especially involved with weak references and memory leaks.
     */
    fun collectGarbage()

    /**
     * A suspending functions that does everything in its power to trigger garbage collection on the given target.
     */
    suspend fun collectGarbageForced()

    /**
     * Logs a value to the platform-specific console (or the standard output).
     */
    fun log(value: Any?)
}

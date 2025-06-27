package dev.toolkt.core.platform

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

expect object PlatformSystem {
    /**
     * Collects garbage in a best-effort manner. This utility is meant to help with testing and debugging memory
     * management issues, especially involved with weak references and memory leaks.
     */
    fun collectGarbage()

    fun log(value: Any?)
}

suspend fun PlatformSystem.collectGarbageSuspend() {
    collectGarbage()
    delay(1.milliseconds)
}

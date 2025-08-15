package dev.toolkt.core.platform

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

actual object PlatformSystem {
    actual fun collectGarbage() {
        System.gc()
    }

    actual suspend fun collectGarbageForced() {
        // On JVM, a double GC call + delay seems to always work

        System.gc()
        delay(1.milliseconds)

        System.gc()
        delay(1.milliseconds)
    }

    actual fun log(value: Any?) {
        println(value)
    }
}

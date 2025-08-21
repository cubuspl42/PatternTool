package dev.toolkt.reactive.system_tests.utils

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private val finalizationRegistry = PlatformFinalizationRegistry()

suspend fun <T : Any> PlatformWeakReference<T>.awaitCollection() {
    PlatformSystem.collectGarbageForced()

    suspendCancellableCoroutine { continuation ->
        val target = get() ?: continuation.resume(Unit)

        val cleanable = finalizationRegistry.register(
            target = target,
        ) {
            continuation.resume(Unit)
        }

        continuation.invokeOnCancellation {
            cleanable.unregister()
        }
    }
}

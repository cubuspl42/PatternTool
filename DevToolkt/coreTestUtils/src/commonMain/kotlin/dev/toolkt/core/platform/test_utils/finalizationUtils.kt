package dev.toolkt.core.platform.test_utils

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private val finalizationRegistry = PlatformFinalizationRegistry()

suspend fun <T : Any> PlatformWeakReference<T>.awaitCollection(
    tag: String,
) {
    PlatformSystem.collectGarbageForced()

    suspendCancellableCoroutine { continuation ->
        val target = get() ?: continuation.resume(Unit)

        val cleanable = finalizationRegistry.register(
            target = target,
        ) {
            continuation.resume(Unit)
        }

        continuation.invokeOnCancellation {
            println("Cancelling the finalization listener for #$tag")

            cleanable.unregister()
        }
    }
}

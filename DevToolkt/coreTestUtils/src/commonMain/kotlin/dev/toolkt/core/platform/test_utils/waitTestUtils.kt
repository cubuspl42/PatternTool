package dev.toolkt.core.platform.test_utils

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

enum class WaitUntilResult {
    Timeout, Success,
}

/**
 * Waits until the [predicate] returns `true`, checking it every [pauseDuration] for a maximum of [timeoutDuration].
 * Puts stress on the garbage collector.
 *
 * @return [WaitUntilResult.Success] if the predicate returned `true` within the timeout, or [WaitUntilResult.Timeout] if it did not.
 */
suspend fun waitUntil(
    pauseDuration: Duration,
    timeoutDuration: Duration,
    predicate: () -> Boolean,
): WaitUntilResult {
    val tryCount = (timeoutDuration / pauseDuration).roundToInt()

    (tryCount downTo 0).forEach { tryIndex ->
        delay(pauseDuration)
        PlatformSystem.collectGarbage()

        if (predicate()) {
            return WaitUntilResult.Success
        }
    }

    return WaitUntilResult.Timeout
}

enum class WaitWhileResult {
    Passed, Failure,
}

/**
 * Waits while the [predicate] returns `true`, checking it every [pauseDuration] for [testDuration].
 * Puts stress on the garbage collector.
 *
 * @return [WaitWhileResult.Passed] if the predicate kept returning `true` within the timeout, or [WaitWhileResult.Failure]
 * if it returned `false` at least one time within the timeout.
 */
suspend fun waitWhile(
    pauseDuration: Duration,
    testDuration: Duration,
    predicate: () -> Boolean,
): WaitWhileResult {
    val tryCount = (testDuration / pauseDuration).roundToInt()

    (tryCount downTo 0).forEach { tryIndex ->
        println("Waiting...")

        PlatformSystem.collectGarbage()

        if (!predicate()) {
            return WaitWhileResult.Failure
        }

        delay(pauseDuration)
    }

    return WaitWhileResult.Passed
}

/**
 * Ensures that the object referenced by [weakRef] is not collected by the garbage collector, despite the stress applied.
 */
suspend fun <T : Any> ensureNotCollected(
    weakRef: PlatformWeakReference<T>,
) {
    // It was empirically proven that a system under test which does NOT
    // correctly ensure that the object is not collected fails this test
    // virtually immediately (after a single iteration)

    waitWhile(
        pauseDuration = 1.milliseconds,
        testDuration = 10.milliseconds,
    ) {
        // Let's ensure that the test fails immediately if the reactive list
        // is collected despite having an observer
        //
        // If it doesn't happen within the testing duration (even though
        // stress is applied on GC), it doesn't formally prove the correctness
        // of the system under test, but in practice it should be enough

        weakRef.get() != null
    }
}

/**
 * Waits for [waitDuration], waking up every [pauseDuration] and putting stress on the garbage collector.
 */
suspend fun waitBusy(
    pauseDuration: Duration,
    waitDuration: Duration,
) {
    val tryCount = (waitDuration / pauseDuration).roundToInt()

    (tryCount downTo 0).forEach { tryIndex ->
        PlatformSystem.collectGarbage()
        delay(pauseDuration)
    }
}

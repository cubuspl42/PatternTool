package dev.toolkt.reactive.system_tests

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

val timeout = 10.seconds

val systemTests = listOf(
    EventStreamSingleTest,
    EventStreamMapAtTest,
    EventStreamHoldTest,
)

suspend fun main() = coroutineScope {
    println("Starting system tests.. (timeout: $timeout)")

    try {
        // Execute all system tests concurrently with a timeout
        withTimeout(timeout = timeout) {
            systemTests.map { systemTest ->
                println("Running system test ${systemTest::class.simpleName}...")

                async {
                    systemTest.executeTest()
                }
            }.awaitAll()
        }

        println("All system tests completed successfully! ✅")
    } catch (_: TimeoutCancellationException) {
        println("System tests timed out! ❌")

        throw AssertionError("System tests timed out")
    }
}

package dev.toolkt.core.async_tests

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AsyncTestSuite {
    open val timeout: Duration = 10.seconds

    abstract val groups: List<AsyncTestGroup>

    suspend fun execute() = coroutineScope {
        println("> Executing async test suite ${this::class.simpleName}...")

        val backgroundJob = launch { runInBackground() }

        try {
            withTimeout(timeout = timeout) {
                groups.flatMap { group ->
                    println(">> Executing async test group ${group::class.simpleName}...")

                    group.tests.map { test ->
                        println(">>> Executing async test ${test::class.simpleName}...")

                        async {
                            test.execute()
                        }
                    }
                }.awaitAll()
            }
        } catch (_: TimeoutCancellationException) {
            println("Async test suite timed out! ❌")

            throw AssertionError("Async test suite timed out")
        } finally {
            backgroundJob.cancel()
        }
    }

    open suspend fun runInBackground() {}
}

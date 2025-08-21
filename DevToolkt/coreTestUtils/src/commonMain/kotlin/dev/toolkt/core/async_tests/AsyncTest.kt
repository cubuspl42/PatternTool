package dev.toolkt.core.async_tests

abstract class AsyncTest {
    abstract suspend fun execute()
}

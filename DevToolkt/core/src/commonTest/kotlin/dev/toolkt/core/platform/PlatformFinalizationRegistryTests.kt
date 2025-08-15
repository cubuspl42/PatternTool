package dev.toolkt.core.platform

import dev.toolkt.core.platform.test_utils.runTestDefault
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlatformFinalizationRegistryTests {
    @Test
    fun testRegister_collection() = runTestDefault {
        val finalizationRegistry = PlatformFinalizationRegistry()

        var cleanCounter = 0

        val arrayWeakRef = PlatformWeakReference(
            Array(1024) { 0 }.also {
                finalizationRegistry.register(
                    target = it,
                ) {
                    ++cleanCounter
                }
            },
        )

        PlatformSystem.collectGarbageForced()

        assertNull(
            actual = arrayWeakRef.get(),
            message = "The array should have been collected.",
        )

        assertEquals(
            expected = 1,
            actual = cleanCounter,
            message = "The cleanup action should have been invoked once.",
        )
    }

    @Test
    fun testClean_once() = runTestDefault {
        val finalizationRegistry = PlatformFinalizationRegistry()

        var cleanCounter = 0

        fun buildArray(): PlatformWeakReference<Array<Int>> {
            val array = Array(1024) { 0 }

            val cleanable = finalizationRegistry.register(
                target = array,
            ) {
                ++cleanCounter
            }

            cleanable.clean()

            assertEquals(
                expected = 1,
                actual = cleanCounter,
                message = "The cleanup action should have been invoked once.",
            )

            return PlatformWeakReference(array)
        }

        val arrayWeakRef = buildArray()

        PlatformSystem.collectGarbageForced()

        assertNull(
            actual = arrayWeakRef.get(),
            message = "The array should have been collected.",
        )

        assertEquals(
            expected = 1,
            actual = cleanCounter,
            message = "The manually cleaned cleanable should not have been invoked again after garbage collection.",
        )
    }

    @Test
    fun testClean_twice() = runTestDefault {
        val finalizationRegistry = PlatformFinalizationRegistry()

        var cleanCounter = 0

        val array = Array(1024) { 0 }

        val cleanable = finalizationRegistry.register(
            target = array,
        ) {
            ++cleanCounter
        }

        // Clean the cleanable twice
        cleanable.clean()
        cleanable.clean()

        assertEquals(
            expected = 1,
            actual = cleanCounter,
            message = "The cleanup action should have been invoked once.",
        )
    }

    @Test
    fun testUnregister() = runTestDefault {
        val finalizationRegistry = PlatformFinalizationRegistry()

        var cleanCounter = 0

        val arrayWeakRef = Array(1024) { 0 }.let { array ->
            val cleanable = finalizationRegistry.register(
                target = array,
            ) {
                ++cleanCounter
            }

            cleanable.unregister()

            // Jut to ensure that it doesn't throw or otherwise blow up
            cleanable.unregister()

            assertEquals(
                expected = 0,
                actual = cleanCounter,
                message = "The unregistering should not have invoked the cleanup action.",
            )

            PlatformWeakReference(array)
        }

        PlatformSystem.collectGarbageForced()

        assertNull(
            actual = arrayWeakRef.get(),
            message = "The array should have been collected.",
        )

        assertEquals(
            expected = 0,
            actual = cleanCounter,
            message = "The manually unregistered cleanable should not have been invoked also after the garbage collection.",
        )
    }
}

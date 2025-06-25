package dev.toolkt.core.platform

import dev.toolkt.core.platform.test_utils.assertEqualsEventually
import dev.toolkt.core.platform.test_utils.runTestDefault
import kotlinx.coroutines.test.TestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PlatformWeakMapSystemTests {
    private data class Key(
        val key: Int,
    ) : Comparable<Key> {
        override fun compareTo(
            other: Key,
        ): Int = compareValuesBy(this, other) { it.key }
    }

    @Test
    @Suppress("AssignedValueIsNeverRead")
    fun testGarbageCollection(): TestResult = runTestDefault(
        duration = 15.seconds,
    ) {
        val key1 = Key(key = 10)

        var key2: Key? = Key(key = 20)

        val key3 = Key(key = 30)

        var key4: Key? = Key(key = 40)

        val weakMap = mutableWeakMapOf<Key, String>()

        weakMap[key1] = "value1"
        weakMap[key2!!] = "value2"
        weakMap[key3] = "value3"
        weakMap[key4!!] = "value4"

        assertEquals(
            expected = 4,
            actual = weakMap.size,
        )

        assertEquals(
            expected = setOf(key1, key2, key3, key4),
            actual = weakMap.keys.toSet(),
        )

        key2 = null
        key4 = null

        assertEqualsEventually(
            pauseDuration = 100.milliseconds,
            timeoutDuration = 10.seconds,
            expected = listOf(key1, key3).sorted(),
            actual = {
                PlatformSystem.collectGarbage()

                weakMap.keys.toList().sorted()
            },
        )
    }
}

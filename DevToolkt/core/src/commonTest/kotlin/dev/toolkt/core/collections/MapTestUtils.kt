package dev.toolkt.core.collections

import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies the consistency of a [Map] against expected entries and control keys.
 */
fun <K, V> Map<K, V>.verifyContent(
    /**
     * Expected entries in the map (in the iteration order).
     */
    entries: List<Pair<K, V>>,
    /**
     * Control keys that should not be present in the map.
     */
    controlKeys: Set<K>,
) {
    assertEquals(
        expected = entries.size,
        actual = size,
        message = "Actual size does not match expected size: expected ${entries.size}, got $size",
    )

    // Actual entries in the iteration order
    val actualEntries = toList()

    assertEquals(
        expected = entries,
        actual = actualEntries,
        message = "Actual entries do not match expected entries: expected $entries, got $actualEntries",
    )

    assertTrue(
        actual = controlKeys.none { controlKey ->
            actualEntries.find { it.first == controlKey } != null
        },
    )

    assertTrue(
        actual = controlKeys.none { contains(it) },
    )
}

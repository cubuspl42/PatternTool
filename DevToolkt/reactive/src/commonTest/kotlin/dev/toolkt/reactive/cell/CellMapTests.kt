package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CellMapTests {
    @Test
    fun testMap_listened() {
        val mutableCell = MutableCell.createExternally(
            initialValue = 0,
        )

        val mappedCell = mutableCell.map { "$it" }

        assertFalse(
            actual = mutableCell.hasListeners,
        )

        assertEquals(
            expected = "0",
            actual = mappedCell.sampleExternally(),
        )

        val newValuesVerifier = EventStreamVerifier(
            eventStream = mappedCell.newValues,
        )

        assertTrue(
            actual = mutableCell.hasListeners,
        )

        mutableCell.setExternally(1)

        assertEquals(
            expected = listOf("1"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = "1",
            actual = mappedCell.sampleExternally(),
        )

        mutableCell.setExternally(2)

        assertEquals(
            expected = listOf("2"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = "2",
            actual = mappedCell.sampleExternally(),
        )
    }

    @Test
    fun testMap_unlistened() {
        val mutableCell = MutableCell.createExternally(
            initialValue = 0,
        )

        val mappedCell = mutableCell.map { "$it" }

        assertFalse(
            actual = mutableCell.hasListeners,
        )

        assertEquals(
            expected = "0",
            actual = mappedCell.sampleExternally(),
        )

        val newValuesVerifier = EventStreamVerifier(
            eventStream = mappedCell.newValues,
        )

        assertTrue(
            actual = mutableCell.hasListeners,
        )

        mutableCell.setExternally(1)

        assertEquals(
            expected = listOf("1"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = "1",
            actual = mappedCell.sampleExternally(),
        )

        mutableCell.setExternally(2)

        assertEquals(
            expected = listOf("2"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = "2",
            actual = mappedCell.sampleExternally(),
        )
    }
}

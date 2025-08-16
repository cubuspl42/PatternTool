package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellMapTests {
    @Test
    fun testMap() {
        val mutableCell = MutableCell(
            initialValue = 0,
        )

        val mappedCell = mutableCell.map { "$it" }

        val changesVerifier = EventStreamVerifier(
            eventStream = mappedCell.changes,
        )

        assertEquals(
            expected = "0",
            actual = mappedCell.currentValue,
        )

        mutableCell.setUnmanaged(1)

        assertEquals(
            expected = "1",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = "0",
                    newValue = "1",
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell.setUnmanaged(2)

        assertEquals(
            expected = "2",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = "1",
                    newValue = "2",
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

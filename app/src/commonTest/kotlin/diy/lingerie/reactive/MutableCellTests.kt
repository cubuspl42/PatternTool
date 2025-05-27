package diy.lingerie.reactive

import diy.lingerie.reactive.cell.Cell
import diy.lingerie.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCellTests {
    @Test
    fun testMutableCell() {
        val mutableCell = MutableCell(
            initialValue = 0,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mutableCell.changes,
        )

        assertEquals(
            expected = 0,
            actual = mutableCell.currentValue,
        )

        mutableCell.set(1)

        assertEquals(
            expected = 1,
            actual = mutableCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 0,
                    newValue = 1,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell.set(2)

        assertEquals(
            expected = 2,
            actual = mutableCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 1,
                    newValue = 2,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

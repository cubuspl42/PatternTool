package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellSwitchTests {
    @Test
    fun testMap() {
        val mutableCell1 = MutableCell.createExternally(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell.createExternally(
            initialValue = -2,
        )

        val mutableNestedCell = MutableCell.createExternally(
            initialValue = mutableCell1,
        )

        val switchedCell = Cell.switch(
            nestedCell = mutableNestedCell,
        )

        val changesVerifier = EventStreamVerifier.setup(
            eventStream = switchedCell.changes,
        )

        assertEquals(
            expected = 10,
            actual = switchedCell.currentValueUnmanaged,
        )

        mutableCell1.setExternally(9)

        mutableCell2.setExternally(-3)

        mutableCell1.setExternally(8)

        assertEquals(
            expected = 8,
            actual = switchedCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 10,
                    newValue = 9,
                ),
                Cell.Change(
                    oldValue = 9,
                    newValue = 8,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableNestedCell.setExternally(mutableCell2)

        assertEquals(
            expected = -3,
            actual = switchedCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 8,
                    newValue = -3,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell1.setExternally(11)

        mutableCell2.setExternally(-4)

        mutableCell1.setExternally(12)

        mutableCell2.setExternally(-5)

        assertEquals(
            expected = -5,
            actual = switchedCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = -3,
                    newValue = -4,
                ),
                Cell.Change(
                    oldValue = -4,
                    newValue = -5,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

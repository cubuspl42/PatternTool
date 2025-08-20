package dev.toolkt.reactive.cell

import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


class CellSlotTests {
    @Test
    fun testCreate() {
        val cellSlot: CellSlot<Int> = CellSlot.createExternally(
            initialValue = 0,
        )

        val eventStreamVerifier = EventStreamVerifier.setup(
            eventStream = cellSlot.newValues,
        )

        assertEquals(
            expected = 0,
            actual = cellSlot.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testBind_once() {
        val cellSlot: CellSlot<Int> = CellSlot.createExternally(
            initialValue = 0,
        )

        val eventStreamVerifier = EventStreamVerifier.setup(
            eventStream = cellSlot.newValues,
        )

        val mutableCell = MutableCell.createExternally(
            initialValue = 10,
        )

        cellSlot.bindExternally(mutableCell)

        assertEquals(
            expected = listOf(10),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = 10,
            actual = cellSlot.sampleExternally(),
        )

        assertEquals(
            expected = 10,
            actual = Actions.external {
                // Set the value once
                mutableCell.set(11)

                // Ensure that it's updated atomically
                cellSlot.sample()
            },
        )

        assertEquals(
            expected = listOf(11),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = 11,
            actual = cellSlot.sampleExternally(),
        )

        assertEquals(
            expected = 11,
            actual = Actions.external {
                // Set the value once more
                mutableCell.set(12)

                // Ensure that it's updated atomically
                cellSlot.sample()
            },
        )

        assertEquals(
            expected = listOf(12),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = 12,
            actual = cellSlot.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testBind_twice() {
        val cellSlot: CellSlot<Int> = CellSlot.createExternally(
            initialValue = 0,
        )

        val eventStreamVerifier = EventStreamVerifier.setup(
            eventStream = cellSlot.newValues,
        )

        val mutableCell1 = MutableCell.createExternally(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell.createExternally(
            initialValue = 20,
        )

        Actions.external {
            // Bind one cell
            cellSlot.bind(mutableCell1)

            // Ensure that it's updated atomically
            mutableCell1.set(11)
        }

        assertEquals(
            expected = listOf(10, 11),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        // Bind another cell
        cellSlot.bindExternally(mutableCell2)

        assertEquals(
            expected = listOf(20),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        // Verify that the cell slot properly unsubscribed
        assertFalse(
            actual = mutableCell1.hasListeners,
        )

        // Sample the new cell
        assertEquals(
            expected = 20, actual = cellSlot.sampleExternally()
        )

        assertEquals(
            expected = 20,
            actual = Actions.external {
                // Update the new cell
                mutableCell2.set(21)

                // Ensure that it's updated atomically
                cellSlot.sample()
            },
        )

        assertEquals(
            expected = listOf(21),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = 21,
            actual = cellSlot.sampleExternally(),
        )

        // Update the original cell
        mutableCell1.setExternally(12)

        // Ensure that it didn't affect the cell slot
        assertEquals(
            expected = 21,
            actual = cellSlot.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )
    }
}

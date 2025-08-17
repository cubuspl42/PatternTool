package dev.toolkt.reactive.future

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureSwitchHoldTests {
    @Test
    fun testSwitchHold() {
        val mutableCell1 = MutableCell(initialValue = -10)
        val mutableCell2 = MutableCell(initialValue = 10)

        val futureCompleter = FutureCompleter<Cell<Int>>()

        val switchHoldCell = futureCompleter.switchHold(
            initialCell = mutableCell1,
        )

        val newValuesVerifier = EventStreamVerifier(
            eventStream = switchHoldCell.newValues,
        )

        mutableCell1.setUnmanaged(-11)
        mutableCell2.setUnmanaged(11)

        assertEquals(
            expected = -11,
            actual = switchHoldCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf(-11),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        futureCompleter.completeUnmanaged(mutableCell2)

        assertEquals(
            expected = 11,
            actual = switchHoldCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf(11),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell1.setUnmanaged(-12)
        mutableCell2.setUnmanaged(12)

        assertEquals(
            expected = 12,
            actual = switchHoldCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf(12),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}


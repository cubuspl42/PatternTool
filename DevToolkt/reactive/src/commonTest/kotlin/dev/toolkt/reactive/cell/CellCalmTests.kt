package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellCalmTests {
    @Test
    fun testCalm_initial() {
        val mutableCell = MutableCell(
            initialValue = 10,
        )

        val calmedCall = mutableCell.calm()

        assertEquals(
            expected = 10,
            actual = calmedCall.currentValue,
        )
    }

    @Test
    fun testCalm_changedToDifferent() {
        val mutableCell = MutableCell(
            initialValue = 10,
        )

        val calmedCall = mutableCell.calm()

        val newValuesVerifier = EventStreamVerifier(
            eventStream = calmedCall.newValues,
        )

        mutableCell.setUnmanaged(20)

        assertEquals(
            expected = listOf(20),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testCalm_changedToSame() {
        val mutableCell = MutableCell(
            initialValue = 10,
        )

        val calmedCall = mutableCell.calm()

        val newValuesVerifier = EventStreamVerifier(
            eventStream = calmedCall.newValues,
        )

        mutableCell.setUnmanaged(10)

        assertEquals(
            expected = emptyList(),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}

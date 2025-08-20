package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellCalmTests {
    @Test
    fun testCalm_initial() {
        val mutableCell = MutableCell.createExternally(
            initialValue = 10,
        )

        val calmedCall = mutableCell.calm()

        assertEquals(
            expected = 10,
            actual = calmedCall.currentValueUnmanaged,
        )
    }

    @Test
    fun testCalm_changedToDifferent() {
        val mutableCell = MutableCell.createExternally(
            initialValue = 10,
        )

        val calmedCall = mutableCell.calm()

        val newValuesVerifier = EventStreamVerifier.setup(
            eventStream = calmedCall.newValues,
        )

        mutableCell.setExternally(20)

        assertEquals(
            expected = listOf(20),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testCalm_changedToSame() {
        val mutableCell = MutableCell.createExternally(
            initialValue = 10,
        )

        val calmedCall = mutableCell.calm()

        val newValuesVerifier = EventStreamVerifier.setup(
            eventStream = calmedCall.newValues,
        )

        mutableCell.setExternally(10)

        assertEquals(
            expected = emptyList(),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}

package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellMap4Tests {
    @Test
    fun testMap4_initial() {
        val mutableCell0 = MutableCell(
            initialValue = 0,
        )

        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell(
            initialValue = 20,
        )

        val mutableCell3 = MutableCell(
            initialValue = 30,
        )

        val mappedCell = Cell.map4(
            cell1 = mutableCell0,
            cell2 = mutableCell1,
            cell3 = mutableCell2,
            cell4 = mutableCell3,
        ) { value1, value2, value3, value4 ->
            "$value1:$value2:$value3:$value4"
        }

        assertEquals(
            expected = "0:10:20:30",
            actual = mappedCell.currentValue,
        )

        mutableCell0.setUnmanaged(1)
    }

    @Test
    fun testMap4_newValue() {
        val mutableCell0 = MutableCell(
            initialValue = 0,
        )

        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell(
            initialValue = 20,
        )

        val mutableCell3 = MutableCell(
            initialValue = 30,
        )

        val mappedCell = Cell.map4(
            cell1 = mutableCell0,
            cell2 = mutableCell1,
            cell3 = mutableCell2,
            cell4 = mutableCell3,
        ) { value1, value2, value3, value4 ->
            "$value1:$value2:$value3:$value4"
        }

        val newValuesVerifier = EventStreamVerifier(
            eventStream = mappedCell.newValues,
        )

        mutableCell0.setUnmanaged(1)

        assertEquals(
            expected = "1:10:20:30",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:10:20:30"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell1.setUnmanaged(11)

        assertEquals(
            expected = "1:11:20:30",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:11:20:30"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell2.setUnmanaged(21)

        assertEquals(
            expected = "1:11:21:30",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:11:21:30"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell3.setUnmanaged(31)

        assertEquals(
            expected = "1:11:21:31",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:11:21:31"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}

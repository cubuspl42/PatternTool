package dev.toolkt.reactive.cell

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellMap3Tests {
    @Test
    fun testMap3_initial() {
        val mutableCell0 = MutableCell(
            initialValue = 0,
        )

        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell(
            initialValue = 20,
        )

        val mappedCell = Cell.map3(
            cell1 = mutableCell0,
            cell2 = mutableCell1,
            cell3 = mutableCell2,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        assertEquals(
            expected = "0:10:20",
            actual = mappedCell.currentValue,
        )

        mutableCell0.set(1)
    }

    @Test
    fun testMap3_newValue() {
        val mutableCell0 = MutableCell(
            initialValue = 0,
        )

        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell(
            initialValue = 20,
        )

        val mappedCell = Cell.map3(
            cell1 = mutableCell0,
            cell2 = mutableCell1,
            cell3 = mutableCell2,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val newValuesVerifier = EventStreamVerifier(
            eventStream = mappedCell.newValues,
        )

        mutableCell0.set(1)

        assertEquals(
            expected = "1:10:20",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:10:20"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell1.set(11)

        assertEquals(
            expected = "1:11:20",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:11:20"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell2.set(21)

        assertEquals(
            expected = "1:11:21",
            actual = mappedCell.currentValue,
        )

        assertEquals(
            expected = listOf("1:11:21"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}


package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class CellMap2Tests {
    @Test
    fun testMap2_initial() {
        val mutableCell0 = MutableCell(
            initialValue = 0,
        )

        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mappedCell = Cell.map2(
            cell1 = mutableCell0,
            cell2 = mutableCell1,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        assertEquals(
            expected = "0:10",
            actual = mappedCell.currentValueUnmanaged,
        )

        mutableCell0.setExternally(1)
    }

    @Test
    fun testMap2_newValue() {
        val mutableCell0 = MutableCell(
            initialValue = 0,
        )

        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mappedCell = Cell.map2(
            cell1 = mutableCell0,
            cell2 = mutableCell1,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val newValuesVerifier = EventStreamVerifier(
            eventStream = mappedCell.newValues,
        )

        mutableCell0.setExternally(1)

        assertEquals(
            expected = "1:10",
            actual = mappedCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf("1:10"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        mutableCell1.setExternally(11)

        assertEquals(
            expected = "1:11",
            actual = mappedCell.currentValueUnmanaged,
        )

        assertEquals(
            expected = listOf("1:11"),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}

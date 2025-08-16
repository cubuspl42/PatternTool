package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListDiffTests {
    @Test
    fun testDiff_initial() {
        val mutableListCell = MutableCell(
            initialValue = listOf(10, 20, 30),
        )

        val diffReactiveList = ReactiveList.diff(mutableListCell)

        assertEquals(
            expected = listOf(10, 20, 30),
            actual = diffReactiveList.currentElements,
        )
    }

    @Test
    fun testDiff_newValue() {
        val mutableListCell = MutableCell(
            initialValue = listOf(10, 20, 30),
        )

        val diffReactiveList = ReactiveList.diff(mutableListCell)


        val changesVerifier = EventStreamVerifier(
            eventStream = diffReactiveList.changes,
        )

        mutableListCell.setUnmanaged(
            listOf(11, 21, 31, 41),
        )

        assertEquals(
            expected = listOf(11, 21, 31, 41),
            actual = diffReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 0..2,
                        changedElements = listOf(11, 21, 31, 41),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testDiff_newEmpty() {
        val mutableListCell = MutableCell(
            initialValue = listOf(10, 20, 30),
        )

        val diffReactiveList = ReactiveList.diff(mutableListCell)


        val changesVerifier = EventStreamVerifier(
            eventStream = diffReactiveList.changes,
        )

        mutableListCell.setUnmanaged(emptyList())

        assertEquals(
            expected = emptyList(),
            actual = diffReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 0..2,
                        changedElements = emptyList(),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

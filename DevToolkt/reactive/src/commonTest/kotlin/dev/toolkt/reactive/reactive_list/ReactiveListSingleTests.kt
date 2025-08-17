package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListSingleTests {
    @Test
    fun testSingle_initial() {
        val mutableCell = MutableCell(initialValue = 10)

        val singleReactiveList = ReactiveList.single(
            element = mutableCell,
        )

        assertEquals(
            expected = listOf(10),
            actual = singleReactiveList.currentElements,
        )
    }

    @Test
    fun testSingle_newValue() {
        val mutableCell = MutableCell(initialValue = 10)

        val singleReactiveList = ReactiveList.single(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )

        mutableCell.setExternally(20)

        assertEquals(
            expected = listOf(20),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(0),
                        changedElements = listOf(20),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListDiffDynamicTests {
    @Test
    fun testDiffDynamic_initial() {
        val initialReactiveList = MutableReactiveList.createExternally(10, 20, 30)

        val mutableReactiveListCell = MutableCell.createExternally<ReactiveList<Int>>(
            initialValue = initialReactiveList,
        )

        val diffReactiveList = ReactiveList.diffDynamic(mutableReactiveListCell)

        val changesVerifier = EventStreamVerifier.listenForever(
            eventStream = diffReactiveList.changes,
        )

        assertEquals(
            expected = listOf(10, 20, 30),
            actual = diffReactiveList.sampleContentExternally(),
        )

        initialReactiveList.setExternally(
            index = 1,
            newValue = 21,
        )

        assertEquals(
            expected = listOf(10, 21, 30),
            actual = diffReactiveList.sampleContentExternally(),
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(21),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testDiffDynamic_newReactiveList() {
        val initialReactiveList = MutableReactiveList.createExternally(10, 20, 30)

        val newReactiveList = MutableReactiveList.createExternally(11, 21, 31, 41)

        val mutableReactiveListCell = MutableCell.createExternally<ReactiveList<Int>>(
            initialValue = initialReactiveList,
        )

        val diffReactiveList = ReactiveList.diffDynamic(mutableReactiveListCell)

        val changesVerifier = EventStreamVerifier.listenForever(
            eventStream = diffReactiveList.changes,
        )

        mutableReactiveListCell.setExternally(newReactiveList)

        assertEquals(
            expected = listOf(11, 21, 31, 41),
            actual = diffReactiveList.sampleContentExternally(),
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
}

package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListSingleNotNullTests {
    @Test
    fun testSingleNotNull_initialNull() {
        val mutableCell = MutableCell<Int?>(initialValue = null)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )

        assertEquals(
            expected = emptyList(),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testSingleNotNull_initialNonNull() {
        val mutableCell = MutableCell<Int?>(initialValue = 10)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )

        assertEquals(
            expected = listOf(10),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testSingleNotNull_changedFromNonNullToNull() {
        val mutableCell = MutableCell<Int?>(initialValue = 10)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )


        mutableCell.set(null)

        assertEquals(
            expected = emptyList(),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        index = 0,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testSingleNotNull_changedFromNullToNonNull() {
        val mutableCell = MutableCell<Int?>(initialValue = null)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )


        mutableCell.set(10)

        assertEquals(
            expected = listOf(10),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.insert(
                        index = 0,
                        newElement = 10,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testSingleNotNull_changedFromNonNullToNonNull() {
        val mutableCell = MutableCell<Int?>(initialValue = 10)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )


        mutableCell.set(20)

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

    @Test
    fun testSingleNotNull_changedFromNullToNull() {
        val mutableCell = MutableCell<Int?>(initialValue = null)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val mutableCellChangesVerifier = EventStreamVerifier(
            eventStream = mutableCell.newValues,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )

        mutableCell.set(null)

        if (mutableCellChangesVerifier.removeReceivedEvents() != listOf(null)) {
            throw AssertionError("Unexpected MutableCell behavior")
        }

        assertEquals(
            expected = listOf(),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class MutableReactiveListTests {
    @Test
    fun testSet() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val changesVerifier = EventStreamVerifier.setup(
            eventStream = mutableReactiveList.changes,
        )

        assertEquals(
            expected = originalContent,
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.setExternally(
            index = 1,
            newValue = 11,
        )

        assertEquals(
            expected = listOf(
                0,
                11,
                20,
                30,
            ),
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(11),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.setExternally(
            index = 3,
            newValue = 31,
        )

        assertEquals(
            expected = listOf(
                0,
                11,
                20,
                31,
            ),
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(3),
                        changedElements = listOf(31),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testRemoveAt() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
            40,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val changesVerifier = EventStreamVerifier.setup(
            eventStream = mutableReactiveList.changes,
        )

        assertEquals(
            expected = originalContent,
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.removeAtExternally(
            index = 3,
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                20,
                40,
            ),
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        index = 3,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.removeAtExternally(
            index = 2,
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                40,
            ),
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        index = 2,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }


    @Test
    fun testAddAll() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val changesVerifier = EventStreamVerifier.setup(
            eventStream = mutableReactiveList.changes,
        )

        assertEquals(
            expected = originalContent,
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.addAllExternally(
            index = 2,
            elements = listOf(11, 12, 13),
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                11,
                12,
                13,
                20,
                30,
            ),
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.insert(
                        index = 2,
                        newElements = listOf(11, 12, 13),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.addAllExternally(
            index = 4,
            elements = listOf(-12, -13),
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                11,
                12,
                -12,
                -13,
                13,
                20,
                30,
            ),
            actual = mutableReactiveList.currentElementsUnmanaged,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.insert(
                        index = 2,
                        newElements = listOf(-12, -13),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

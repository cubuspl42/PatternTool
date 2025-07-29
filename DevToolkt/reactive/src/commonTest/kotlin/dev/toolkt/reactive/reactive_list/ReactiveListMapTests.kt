package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.event_stream.listenExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListMapTests {
    @Test
    fun testMap_initial() {
        val mutableReactiveList = MutableReactiveList(
            initialContent = listOf(
                0,
                10,
                20,
                30,
            ),
        )

        val mappedList = mutableReactiveList.map { -it }

        assertEquals(
            expected = listOf(
                0,
                -10,
                -20,
                -30,
            ),
            actual = mappedList.currentElements,
        )
    }

    @Test
    fun testMap_update() {
        val mutableReactiveList = MutableReactiveList(
            initialContent = listOf(
                0,
                10,
                20,
                30,
            ),
        )

        val mappedList = mutableReactiveList.map { it.toString() }

        val changesVerifier = EventStreamVerifier(
            eventStream = mappedList.changes,
        )

        mutableReactiveList.set(
            index = 2,
            newValue = 21,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(2),
                        changedElements = listOf("21"),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = listOf(
                "0",
                "10",
                "21",
                "30",
            ),
            actual = mappedList.currentElements,
        )
    }

    @Test
    fun testMap_insert() {
        val mutableReactiveList = MutableReactiveList(
            initialContent = listOf(
                0,
                10,
                20,
                30,
            ),
        )

        val mappedList = mutableReactiveList.map { it.toString() }

        val changesVerifier = EventStreamVerifier(
            eventStream = mappedList.changes,
        )

        mutableReactiveList.addAll(
            index = 2,
            elements = listOf(15, 16),
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(2),
                        changedElements = listOf("15", "16"),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = listOf(
                "0",
                "10",
                "15",
                "16",
                "20",
                "30",
            ),
            actual = mappedList.currentElements,
        )
    }

    @Test
    fun testMap_remove() {
        val mutableReactiveList = MutableReactiveList(
            initialContent = listOf(
                0,
                10,
                20,
                30,
            ),
        )

        val mappedList = mutableReactiveList.map { it.toString() }

        val changesVerifier = EventStreamVerifier(
            eventStream = mappedList.changes,
        )

        mutableReactiveList.removeAt(
            index = 1,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = listOf(
                "0",
                "20",
                "30",
            ),
            actual = mappedList.currentElements,
        )
    }

    @Test
    fun testMap_changeApplication() {
        val mutableReactiveList = MutableReactiveList(
            initialContent = listOf(
                0,
                10,
                20,
            ),
        )

        val mappedList = mutableReactiveList.map { it.toString() }

        var observedCurrentElements: List<String> = listOf()

        mappedList.changes.listenExternally {
            observedCurrentElements = mappedList.currentElements
        }

        mutableReactiveList.set(
            index = 1,
            newValue = 11,
        )

        assertEquals(
            expected = listOf("0", "10", "20"),
            actual = observedCurrentElements,
        )

        assertEquals(
            expected = listOf("0", "11", "20"),
            actual = mappedList.currentElements,
        )
    }
}

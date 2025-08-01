package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListConcatAllTests {
    @Test
    fun testConcatAll_initial() {
        val mutableReactiveList0 = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList1 = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0,
                mutableReactiveList1,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                110,
                120,
                130,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )
    }

    @Test
    fun testConcatAll_innerListUpdated() {
        val mutableReactiveList0 = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList1 = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0,
                mutableReactiveList1,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        val changesVerifier = EventStreamVerifier(
            eventStream = concatenatedReactiveList.changes,
        )

        mutableReactiveList1.set(1, 121)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                110,
                121,
                130,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(4),
                        changedElements = listOf(121),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testConcatAll_innerListExpanded() {
        val mutableReactiveList0 = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList1 = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0,
                mutableReactiveList1,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        val changesVerifier = EventStreamVerifier(
            eventStream = concatenatedReactiveList.changes,
        )

        mutableReactiveList1.addAll(2, listOf(121, 122))

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                110,
                120,
                121,
                122,
                130,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(5),
                        changedElements = listOf(121, 122),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Update the list after the expanded one
        mutableReactiveList2.set(0, 211)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                110,
                120,
                121,
                122,
                130,
                211,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        // Ensure that the indices are shifted
        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(8),
                        changedElements = listOf(211),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testConcatAll_innerListShrank() {
        val mutableReactiveList0 = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList1 = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0,
                mutableReactiveList1,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        val changesVerifier = EventStreamVerifier(
            eventStream = concatenatedReactiveList.changes,
        )

        mutableReactiveList1.removeRange(indexRange = 1..2)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                110,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 4..5,
                        changedElements = listOf(),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Update the list after the shrank one
        mutableReactiveList2.set(1, 221)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                110,
                210,
                221,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        // Ensure that the indices are shifted
        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(5),
                        changedElements = listOf(221),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testConcatAll_outerListUpdated() {
        val mutableReactiveList0 = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList1a = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList1b = MutableReactiveList(
            initialContent = listOf(
                -110,
                -120,
                -130,
            ),
        )


        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0,
                mutableReactiveList1a,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        val changesVerifier = EventStreamVerifier(
            eventStream = concatenatedReactiveList.changes,
        )

        mutableReactiveLists.set(1, mutableReactiveList1b)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                -110,
                -120,
                -130,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 3..5,
                        changedElements = listOf(-110, -120, -130),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testConcatAll_outerListExpanded() {
        val mutableReactiveList0a = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList0b = MutableReactiveList(
            initialContent = listOf(
                40,
                50,
            ),
        )

        val mutableReactiveList0c = MutableReactiveList(
            initialContent = listOf(
                60,
            ),
        )

        val mutableReactiveList1 = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0a,
                mutableReactiveList1,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        val changesVerifier = EventStreamVerifier(
            eventStream = concatenatedReactiveList.changes,
        )

        mutableReactiveLists.addAll(1, listOf(mutableReactiveList0b, mutableReactiveList0c))

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                40,
                50,
                60,
                110,
                120,
                130,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(3),
                        changedElements = listOf(40, 50, 60),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testConcatAll_outerListShrank() {
        val mutableReactiveList0 = MutableReactiveList(
            initialContent = listOf(
                10,
                20,
                30,
            ),
        )

        val mutableReactiveList1 = MutableReactiveList(
            initialContent = listOf(
                110,
                120,
                130,
            ),
        )

        val mutableReactiveList2 = MutableReactiveList(
            initialContent = listOf(
                210,
                220,
                230,
            ),
        )

        val mutableReactiveLists = MutableReactiveList(
            initialContent = listOf(
                mutableReactiveList0,
                mutableReactiveList1,
                mutableReactiveList2,
            ),
        )

        val concatenatedReactiveList = ReactiveList.concatAll(mutableReactiveLists)

        val changesVerifier = EventStreamVerifier(
            eventStream = concatenatedReactiveList.changes,
        )

        mutableReactiveLists.removeAt(1)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
                210,
                220,
                230,
            ),
            actual = concatenatedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 3..5,
                        changedElements = listOf(),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

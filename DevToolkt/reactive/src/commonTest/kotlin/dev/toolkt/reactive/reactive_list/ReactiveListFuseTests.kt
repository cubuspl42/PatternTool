package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListFuseTests {
    @Test
    fun testFuse() {
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)

        val mutableCell2a = MutableCell(initialValue = 210)
        val mutableCell2b = MutableCell(initialValue = 220)

        val mutableCell3 = MutableCell(initialValue = 300)
        val mutableCell4 = MutableCell(initialValue = 400)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell1,
                mutableCell2,
                mutableCell3,
                mutableCell4,
            ),
        )

        val fuseReactiveList = ReactiveList.fuse(
            cells = mutableCells,
            behavior = ReactiveList.Behavior.Forward,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = fuseReactiveList.changes,
        )

        assertEquals(
            expected = listOf(100, 200, 300, 400),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate one of the fused cells
        mutableCell2.set(201)

        assertEquals(
            expected = listOf(100, 201, 300, 400),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(201),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Add some new cells
        mutableCells.addAll(
            index = 2,
            elements = listOf(mutableCell2a, mutableCell2b),
        )

        assertEquals(
            expected = listOf(100, 201, 210, 220, 300, 400),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(3),
                        changedElements = listOf(210, 220),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate one of the original cells after adding a new one
        mutableCell4.set(401)

        assertEquals(
            expected = listOf(100, 201, 210, 220, 300, 401),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(5),
                        changedElements = listOf(401),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Remove one of the cells
        mutableCells.removeAt(0)

        assertEquals(
            expected = listOf(201, 210, 220, 300, 401),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(0),
                        changedElements = emptyList<Int>(),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate one of the original cells after removing another
        mutableCell3.set(301)

        assertEquals(
            expected = listOf(201, 210, 220, 301, 401),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(3),
                        changedElements = listOf(301),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}

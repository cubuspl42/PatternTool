package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.splitBefore
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.ensureNotCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class ReactiveListFuseTests {
    /**
     * Given a reactive list of cells (potentially containing duplicate cells),
     * the fused reactive list's initial current values should be the initial
     * current values of the respective cells in the original list. Later, the
     * fused list should reflect the new values of the cells in the original list.
     */
    @Test
    fun testFuse_initial() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)
        val mutableCell3 = MutableCell(initialValue = 300)
        val mutableCell4 = MutableCell(initialValue = 400)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
                mutableCell1,
                mutableCell2,
                mutableCell3,
                mutableCell4,
                mutableCell2, // A duplicate
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
            expected = listOf(0, 100, 200, 300, 400, 200),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate some of the original cells
        mutableCell1.set(101)
        mutableCell2.set(201) // A duplicate

        assertEquals(
            expected = listOf(0, 101, 201, 300, 400, 201),
            actual = fuseReactiveList.currentElements,
        )

        val nonDeterministicChanges = changesVerifier.removeReceivedEvents()
        val (orderedChanges, unorderedChanges) = nonDeterministicChanges.splitBefore(index = 1)

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(101),
                    ),
                ),
            ),
            actual = orderedChanges,
        )

        // The order is non-deterministic (!)
        assertEquals(
            expected = setOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(2),
                        changedElements = listOf(201),
                    ),
                ),
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(5),
                        changedElements = listOf(201),
                    ),
                ),
            ),
            actual = unorderedChanges.toSet(),
        )
    }

    /**
     * Given a reactive list of cells, when the original list changes in such
     * a way that the list size changes (more cells were added than removed,
     * or vice versa), the changes of the fused list should reflect this.
     * The new values of the original cells on indices before the changed range
     * should be reported under unchanged indices, but the new values of the
     * cells on the right of the changed range should be reported under
     * appropriately adjusted indices.
     */
    @Test
    fun testFuse_indexShift() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)
        val mutableCell3 = MutableCell(initialValue = 300)

        val mutableCell3a = MutableCell(initialValue = 310)
        val mutableCell3b = MutableCell(initialValue = 320)
        val mutableCell3c = MutableCell(initialValue = 330)

        val mutableCell4 = MutableCell(initialValue = 400)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
                mutableCell1,
                mutableCell2,
                mutableCell3,
                mutableCell4,
            ),
        )

        val fuseReactiveList = ReactiveList.fuse(
            cells = mutableCells,
            behavior = ReactiveList.Behavior.Cache,
        )

        mutableCells.replaceAll(
            indexRange = 2..3,
            changedElements = listOf(
                mutableCell3a,
                mutableCell3b,
                mutableCell3c,
            ),
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = fuseReactiveList.changes,
        )

        // Mutate a cell from the left side
        mutableCell1.set(101)

        // This seems to fail non-deterministically...
        assertEquals(
            expected = listOf(0, 101, 310, 320, 330, 400),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(101),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate a cell from the right side
        mutableCell4.set(401)

        assertEquals(
            expected = listOf(0, 101, 310, 320, 330, 401),
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

        // Mutate the original list again, removing some of the new cells
        // and some of the original cells
        mutableCells.removeRange(1..3)

        changesVerifier.removeReceivedEvents()

        if (fuseReactiveList.currentElements != listOf(0, 330, 401)) {
            throw AssertionError("Unexpected current elements: ${fuseReactiveList.currentElements}")
        }

        // Mutate all remaining cells
        mutableCell0.set(1)
        mutableCell3c.set(331)
        mutableCell4.set(402)

        assertEquals(
            expected = listOf(1, 331, 402),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(0),
                        changedElements = listOf(1),
                    ),
                ),
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(331),
                    ),
                ),
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(2),
                        changedElements = listOf(402),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    /**
     * Given a reactive list of cells, when the original list changes in such
     * a way that new cells are inserted at a given index (potentially inserting
     * duplicate cells), the fused list should reflect this change (both at the
     * moment when the new cells are inserted, and later when their own value
     * changes).
     */
    @Test
    fun testFuse_outerChange_insertedCells() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)

        val mutableCell2a = MutableCell(initialValue = 210)
        val mutableCell2b = MutableCell(initialValue = 220)

        val mutableCell3 = MutableCell(initialValue = 300)
        val mutableCell4 = MutableCell(initialValue = 400)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
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

        mutableCells.addAll(
            index = 3,
            elements = listOf(
                mutableCell2a,
                mutableCell3, // A duplicate in the list
                mutableCell2b,
                mutableCell2a, // A duplicate in the new cells
            ),
        )

        assertEquals(
            expected = listOf(
                0, 100, 200, 210, 300, 220, 210, 300, 400,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(3),
                        changedElements = listOf(210, 300, 220, 210),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate some of the new cells
        mutableCell2a.set(211) // A cell duplicated in the new cells
        mutableCell2b.set(221) // A unique cell
        mutableCell3.set(301) // A cell duplicated in the original list

        assertEquals(
            expected = listOf(
                0, 100, 200, 211, 301, 221, 211, 301, 400,
            ),
            actual = fuseReactiveList.currentElements,
        )

        val outerNonDeterministicChanges = changesVerifier.removeReceivedEvents()
        val (innerNonDeterministicChanges, trailingUnorderedChanges) = outerNonDeterministicChanges.splitBefore(index = 3)
        val (leadingUnorderedChanges, orderedChanges) = innerNonDeterministicChanges.splitBefore(index = 2)

        // The order is non-deterministic (!)
        assertEquals(
            expected = setOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(6),
                        changedElements = listOf(211),
                    ),
                ),
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(3),
                        changedElements = listOf(211),
                    ),
                ),
            ),
            actual = leadingUnorderedChanges.toSet(),
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(5),
                        changedElements = listOf(221),
                    ),
                ),
            ),
            actual = orderedChanges,
        )

        // The order is non-deterministic (!)
        assertEquals(
            expected = setOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(4),
                        changedElements = listOf(301),
                    ),
                ),
                // A duplicate change
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(7),
                        changedElements = listOf(301),
                    ),
                ),
            ),
            actual = trailingUnorderedChanges.toSet(),
        )
    }

    /**
     * Given a reactive list of cells, when the original list changes in such
     * a way that some cells are removed at a given index range, the fused list
     * should reflect this change at the moment when the cells are
     * removed. Later, new values of the removed cells should not affect the
     * fused list.
     */
    // Let's test a bad case, when one of the removed cells is duplicated in
    // the list (at some other, unaffected index)
    @Test
    fun testFuse_outerChange_removedCells() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)
        val mutableCell3 = MutableCell(initialValue = 300)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
                mutableCell1, // A duplicate cell to be removed
                mutableCell2,
                mutableCell3,
                mutableCell1,
            ),
        )

        val fuseReactiveList = ReactiveList.fuse(
            cells = mutableCells,
            behavior = ReactiveList.Behavior.Cache,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = fuseReactiveList.changes,
        )

        mutableCells.removeRange(
            indexRange = 1..2,
        )

        assertEquals(
            expected = listOf(
                0, 300, 100,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        indexRange = 1..2,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate the removed cells
        mutableCell1.set(101) // A duplicate
        mutableCell2.set(201) // A unique cell

        assertEquals(
            expected = listOf(
                0, 300, 101,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(2),
                        changedElements = listOf(101),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    /**
     * Given a reactive list of cells, when the original list changes in such
     * a way that some cells at a given index range are replaced with new cells
     * (potentially a different number of cells), the fused list should
     * reflect this change at the moment when the cells are replaced. Later,
     * new values of the replaced cells should not affect the fused list, but
     * the new values of the replacing cells should.
     */
    @Test
    fun testFuse_outerChange_updatedCells() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)

        val mutableCell2a = MutableCell(initialValue = 210)
        val mutableCell2b = MutableCell(initialValue = 220)
        val mutableCell2c = MutableCell(initialValue = 230)

        val mutableCell3 = MutableCell(initialValue = 300)
        val mutableCell4 = MutableCell(initialValue = 400)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
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

        mutableCells.replaceAll(
            indexRange = 2..3,
            changedElements = listOf(
                mutableCell2a,
                mutableCell2b,
                mutableCell2c,
            ),
        )

        assertEquals(
            expected = listOf(
                0, 100, 210, 220, 230, 400,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 2..3,
                        changedElements = listOf(210, 220, 230),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate some of the changed cells
        mutableCell3.set(301) // A removed cell
        mutableCell2b.set(221) // A new cell

        assertEquals(
            expected = listOf(
                0, 100, 210, 221, 230, 400,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(3),
                        changedElements = listOf(221),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    /**
     * Given a reactive list of cells, when the original list changes in such
     * a way that some cells at a given index range are replaced with new cells,
     * but one or more of the cells is actually one of the cells that are
     * supposedly to be removed, the fused list behaves as if those cells were
     * two different cells that behave equivalently.
     */
    @Test
    fun testFuse_outerChange_updatedCells_instantReAdd() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)

        val mutableCell2a = MutableCell(initialValue = 210)

        val mutableCell3 = MutableCell(initialValue = 300)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
                mutableCell1,
                mutableCell2,
                mutableCell3,
            ),
        )

        val fuseReactiveList = ReactiveList.fuse(
            cells = mutableCells,
            behavior = ReactiveList.Behavior.Cache,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = fuseReactiveList.changes,
        )

        mutableCells.replaceAll(
            indexRange = 1..2,
            changedElements = listOf(
                mutableCell2, // Instant re-add
                mutableCell2a,
            ),
        )

        assertEquals(
            expected = listOf(
                0, 200, 210, 300,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = 1..2,
                        changedElements = listOf(200, 210),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        // Mutate some of the changed cells
        mutableCell2.set(201) // A not really removed cell
        mutableCell1.set(101) // A removed cell

        assertEquals(
            expected = listOf(
                0, 201, 210, 300,
            ),
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
    }

    /**
     * Given a reactive list of cells, when...
     *
     * - one or more change subscriptions are added
     * - the original list and/or its inner cells potentially change
     * - all change subscriptions are cancelled
     * - the original list and/or its inner cells potentially change again
     * - a new change subscription is added
     *
     * ...then the current values of the fused cells and its further change
     * events should correctly reflect the changes from the original list.
     */
    @Test
    fun testFuse_resubscribe() {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 100)
        val mutableCell2 = MutableCell(initialValue = 200)
        val mutableCell3 = MutableCell(initialValue = 300)

        val mutableCell3b = MutableCell(initialValue = 310)

        val mutableCells = MutableReactiveList(
            initialContent = listOf(
                mutableCell0,
                mutableCell1,
                mutableCell2,
                mutableCell3,
            ),
        )

        val fuseReactiveList = ReactiveList.fuse(
            cells = mutableCells,
            behavior = ReactiveList.Behavior.Cache,
        )

        val changesVerifier1 = EventStreamVerifier(
            eventStream = fuseReactiveList.changes,
        )

        mutableCells.removeAt(2)
        mutableCell1.set(101)

        changesVerifier1.cancel()
        changesVerifier1.removeReceivedEvents()

        mutableCells.append(mutableCell3b)

        val changesVerifier2 = EventStreamVerifier(
            eventStream = fuseReactiveList.changes,
        )

        assertEquals(
            expected = listOf(
                0, 101, 300, 310,
            ),
            actual = fuseReactiveList.currentElements,
        )

        mutableCell3.set(301)

        assertEquals(
            expected = listOf(
                0, 101, 301, 310,
            ),
            actual = fuseReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(2),
                        changedElements = listOf(301),
                    ),
                ),
            ),
            actual = changesVerifier2.removeReceivedEvents(),
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier1.removeReceivedEvents(),
        )
    }

    @Test
    @Ignore // FIXME: fuse does not manage memory properly (no stateful entities do?)
    fun testFuse_garbageCollection() = runTestDefault(
        timeout = 2.seconds,
    ) {
        val mutableCell0 = MutableCell(initialValue = 0)
        val mutableCell1 = MutableCell(initialValue = 10)

        fun setup(): Pair<PlatformWeakReference<ReactiveList<Int>>, EventStreamVerifier<ReactiveList.Change<Int>>> {
            val fuseReactiveList = ReactiveList.fuse(
                cells = ReactiveList.of(
                    mutableCell0,
                    mutableCell1,
                ),
            )

            val changesVerifier = EventStreamVerifier(
                eventStream = fuseReactiveList.changes,
            )

            return Pair(
                PlatformWeakReference(fuseReactiveList),
                changesVerifier,
            )
        }

        val (outReactiveListWeakRef, changesVerifier) = setup()

        ensureNotCollected(weakRef = outReactiveListWeakRef)

        mutableCell1.set(11)

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
    }
}

package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.collectGarbageSuspend
import dev.toolkt.core.platform.test_utils.ensureNotCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

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

    /**
     * Ensure that a stateful operator keeps an up-to-date state even when it has no observers
     */
    @Test
    fun testSingleNotNull_sampleOnly() = runTestDefault {
        val mutableCell = MutableCell<Int?>(initialValue = null)

        val singleReactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        PlatformSystem.collectGarbageSuspend()

        mutableCell.set(10)

        assertEquals(
            expected = listOf(10),
            actual = singleReactiveList.currentElements,
        )
    }

    /**
     * [ReactiveList.singleNotNull] is a stateful operator, involving weak references, so it's reasonable to test
     * how it behaves when some stress is applied to the garbage collector.
     */
    @Test
    @Ignore // FIXME: singleNotNull does not manage memory properly (no stateful streams do?)
    fun testSingleNotNull_garbageCollection() = runTestDefault(
        timeout = 10.seconds,
    ) {
        val mutableCell = MutableCell<Int?>(initialValue = 10)

        fun setup(): Pair<PlatformWeakReference<ReactiveList<Int>>, EventStreamVerifier<ReactiveList.Change<Int>>> {
            val singleNotNullReactiveList = ReactiveList.singleNotNull(
                element = mutableCell,
            )

            val changesVerifier = EventStreamVerifier(
                eventStream = singleNotNullReactiveList.changes,
            )

            return Pair(
                PlatformWeakReference(singleNotNullReactiveList),
                changesVerifier,
            )
        }

        val (outReactiveListWeakRef, changesVerifier) = setup()

        ensureNotCollected(weakRef = outReactiveListWeakRef)

        mutableCell.set(null)

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
}

package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.ensureCollected
import dev.toolkt.core.platform.test_utils.ensureNotCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.core.range.single
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

        mutableCell.setExternally(null)

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


        mutableCell.setExternally(10)

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

        mutableCell.setExternally(null)

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

    @Test
    fun testSingleNotNull_unused() = runTestDefault {
        val mutableCell = MutableCell<Int?>(initialValue = 10)

        ReactiveList.singleNotNull(
            element = mutableCell,
        ).changes

        PlatformSystem.collectGarbageForced()

        assertFalse(mutableCell.hasListeners)
    }

    @Test
    fun testSingleNotNull_sampleOnly() = runTestDefault {
        val mutableCell = MutableCell<Int?>(initialValue = null)

        suspend fun testKeepAlive(): PlatformWeakReference<ReactiveList<Int>> {
            val reactiveList = ReactiveList.singleNotNull(
                element = mutableCell,
            )

            PlatformSystem.collectGarbageForced()

            mutableCell.setExternally(10)

            assertEquals(
                expected = listOf(10),
                actual = reactiveList.currentElements,
            )

            return PlatformWeakReference(reactiveList)
        }

        val reactiveListWeakRef = testKeepAlive()

        ensureCollected(
            weakRef = reactiveListWeakRef,
        )

        assertFalse(mutableCell.hasListeners)
    }

    @Test
    fun testSingleNotNull_changesOnly() = runTestDefault {
        val mutableCell = MutableCell<Int?>(initialValue = null)

        val reactiveList = ReactiveList.singleNotNull(
            element = mutableCell,
        )

        val changes = reactiveList.changes

        PlatformSystem.collectGarbageForced()

        mutableCell.setExternally(10)

        val changesVerifier = EventStreamVerifier(
            eventStream = changes,
        )

        mutableCell.setExternally(20)

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

        changesVerifier.cancel()

        assertFalse(mutableCell.hasListeners)
    }

    /**
     * [ReactiveList.singleNotNull] is a stateful operator, involving weak references, so it's reasonable to test
     * how it behaves when some stress is applied to the garbage collector.
     */
    @Test
    fun testSingleNotNull_garbageCollection() = runTestDefault(
        timeout = 10.seconds,
    ) {
        // FIXME: This test now passes, but has serious issues... We don't care about `singleNotNull` reactive list being collected
        //  (unless we store a strong reference, obviously) as long as the operator behaves correctly.
        //  In fact, for RL operators in general, we'd _prefer_ the reactive list object to be collected if it's not needed
        //  anymore, yet from the client's perspective it might not be trivial to decide whether the object is or is not
        //  actually needed.

        // Good tests ideas:

        // 1.
        // Store a strong reference to a RL. Don't check if the RL is collected, as clearly it won't be.
        // Don't add any listeners (don't install a changesVerifier). Put pressure on the GC. Mutate the source.
        // Sample the RL, prove that it changed even though there are no listeners. This test ensures correctness,
        // does not attempt to prove correct memory management (although the reason for it to _fail_ could be a
        // badly implemented memory management focused on ensuring no memory leaks...).

        // 2.
        // Testing the opposite, i.e. the fact that _something_ was GCed when it should've been, might be tricky
        // for `singleNotNull`, as its content is used for building new changes... But on the other hand,
        // if there are no subscriptions and no "samplers", the (potentially heavy) RL and the inner list should not
        // be kept alive, while a serious bug in the memory management _could_ cause that. So maybe it's worth testing.
        // For other potential RL operators, like cons/hold (given changes -> RL) this would be less tricky.

        // 2.
        //

        // GC scenarios:

        // Restorable accumulating stateful reactive entity (e.g. ReactiveList/singleNotNull):

        // Entity is referred -> keep alive (we need the state up-to-date for sampling)
        // Changes is referred (but not listened to!) -> let it go? We can recompute the state at any time, we just
        // have to ensure not to ever emit double remove event. If there were no listeners, that's guaranteed.
        // Otherwise -> let it go

        // Accumulating stateful reactive entity (e.g. Cell/accum):

        // Entity is referred -> keep alive (we need the state up-to-date for sampling)
        // Changes is referred (even when not listened to!) -> keep alive (we have to keep the old state alive, as it
        // can affect the next change and there's no way to recompute it)
        // Otherwise -> let it go

        // Does it introduce cycles that the GC can't break on its own? Do we really need a custom quasi-GC?

        // Non-accumulating stateful reactive entity (e.g. Cell/hold):

        // Entity is referred -> keep alive
        // Otherwise -> let it go

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

        mutableCell.setExternally(null)

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

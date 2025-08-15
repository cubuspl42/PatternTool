package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.ensureCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamHoldTests {
    @Test
    fun testHold() {
        val eventEmitter = EventEmitter<Int>()

        val heldCell = eventEmitter.hold(0)

        val changesVerifier = EventStreamVerifier(
            eventStream = heldCell.changes,
        )

        assertEquals(
            expected = 0,
            actual = heldCell.currentValue,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        eventEmitter.emit(3)

        assertEquals(
            expected = 3,
            actual = heldCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 0,
                    newValue = 2,
                ),
                Cell.Change(
                    oldValue = 2,
                    newValue = 3,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testHold_sampleOnly() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val heldCell = eventEmitter.hold(0)

        assertEquals(
            expected = 0,
            actual = heldCell.currentValue,
        )

        PlatformSystem.collectGarbageForced()

        eventEmitter.emit(2)

        assertEquals(
            expected = 2,
            actual = heldCell.currentValue,
        )
    }

    @Test
    fun testHold_newValuesOnly() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        fun setup(): Pair<PlatformWeakReference<Cell<Int>>, EventStream<Int>> {
            val holdCell = eventEmitter.hold(0)

            return Pair(
                PlatformWeakReference(holdCell),
                holdCell.newValues,
            )
        }

        val (cellWeakRef, newValues) = setup()

        ensureCollected(weakRef = cellWeakRef)

        val newValuesVerifier = EventStreamVerifier(
            eventStream = newValues,
        )

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = newValuesVerifier.removeReceivedEvents(),
        )
    }
}

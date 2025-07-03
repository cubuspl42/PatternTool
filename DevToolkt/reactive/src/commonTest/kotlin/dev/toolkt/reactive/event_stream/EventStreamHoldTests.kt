package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.collectGarbageSuspend
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import dev.toolkt.reactive.cell.Cell
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

        PlatformSystem.collectGarbageSuspend()

        eventEmitter.emit(2)

        assertEquals(
            expected = 2,
            actual = heldCell.currentValue,
        )
    }
}

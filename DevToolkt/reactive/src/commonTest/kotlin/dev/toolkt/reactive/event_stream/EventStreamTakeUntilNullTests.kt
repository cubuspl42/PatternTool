package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.Moments
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventStreamTakeUntilNullTests {
    @Test
    fun testTakeUntilNull() {
        val eventEmitter = EventEmitter.createExternally<Int?>()

        val takeStream = Moments.external {
            eventEmitter.takeUntilNull()
        }

        assertTrue(
            actual = eventEmitter.hasListeners,
        )

        val changesVerifier = EventStreamVerifier.listenForever(
            eventStream = takeStream,
        )

        eventEmitter.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(20)

        assertEquals(
            expected = listOf(20),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(null)

        assertFalse(
            actual = eventEmitter.hasListeners,
        )

        eventEmitter.emitExternally(30)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

}

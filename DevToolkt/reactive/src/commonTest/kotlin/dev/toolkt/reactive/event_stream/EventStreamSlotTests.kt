package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.bindExternally
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.emitExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class EventStreamSlotTests {
    @Test
    fun testCreate() {
        val eventStreamSlot = EventStreamSlot.createExternally<Int>()

        val eventStreamVerifier = EventStreamVerifier(
            eventStream = eventStreamSlot,
        )

        assertEquals(
            expected = emptyList(),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testBind_once() {
        val eventStreamSlot = EventStreamSlot.createExternally<Int>()

        val eventStreamVerifier = EventStreamVerifier(
            eventStream = eventStreamSlot,
        )

        val eventEmitter = EventEmitter.createExternally<Int>()

        // Bind the event stream
        eventStreamSlot.bindExternally(eventEmitter)

        // Emit an event
        eventEmitter.emitExternally(11)

        assertEquals(
            expected = listOf(11),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        // Emit another event
        eventEmitter.emitExternally(12)

        assertEquals(
            expected = listOf(12),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testBind_twice() {
        val eventEmitter1 = EventEmitter.createExternally<Int>()

        val eventEmitter2 = EventEmitter.createExternally<Int>()

        val eventStreamSlot: EventStreamSlot<Int> = EventStreamSlot.createExternally()

        val eventStreamVerifier = EventStreamVerifier(
            eventStream = eventStreamSlot,
        )

        // Bind one event stream
        eventStreamSlot.bindExternally(eventEmitter1)

        // Bind another event stream
        eventStreamSlot.bindExternally(eventEmitter2)

        // Verify that the event stream slot correctly unsubscribed
        assertFalse(
            actual = eventEmitter1.hasListeners,
        )

        // Emit an event from the first event stream
        eventEmitter1.emitExternally(-10)

        // Verify that it wasn't processed
        assertEquals(
            expected = emptyList(),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )

        // Emit an event from the second event stream
        eventEmitter1.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = eventStreamVerifier.removeReceivedEvents(),
        )
    }
}

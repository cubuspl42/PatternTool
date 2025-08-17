package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamFilterTests {
    @Test
    fun testFilter() {
        val eventEmitter = EventEmitter<Int>()

        val mappedStream = eventEmitter.filter { it % 2 == 0 }

        val streamVerifier = EventStreamVerifier(
            eventStream = mappedStream,
        )

        eventEmitter.emitUnmanaged(2)

        eventEmitter.emitUnmanaged(3)

        eventEmitter.emitUnmanaged(5)

        eventEmitter.emitUnmanaged(4)

        assertEquals(
            expected = listOf(2, 4),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitUnmanaged(3)


        eventEmitter.emitUnmanaged(5)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

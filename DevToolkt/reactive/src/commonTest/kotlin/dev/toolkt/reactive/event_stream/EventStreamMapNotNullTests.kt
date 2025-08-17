package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMapNotNullTests {
    @Test
    fun testMapNotNull() {
        val eventEmitter = EventEmitter<Int>()

        val mappedStream = eventEmitter.mapNotNull {
            when {
                it % 2 == 0 -> "$it"
                else -> null
            }
        }

        val streamVerifier = EventStreamVerifier(
            eventStream = mappedStream,
        )

        eventEmitter.emitUnmanaged(2)

        eventEmitter.emitUnmanaged(3)

        eventEmitter.emitUnmanaged(5)

        eventEmitter.emitUnmanaged(4)

        assertEquals(
            expected = listOf("2", "4"),
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

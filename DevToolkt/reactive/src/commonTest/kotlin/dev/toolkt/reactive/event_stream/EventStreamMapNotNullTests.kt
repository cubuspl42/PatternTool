package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMapNotNullTests {
    @Test
    fun testMapNotNull() {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val mappedStream = eventEmitter.mapNotNull {
            when {
                it % 2 == 0 -> "$it"
                else -> null
            }
        }

        val streamVerifier = EventStreamVerifier.listenForever(
            eventStream = mappedStream,
        )

        eventEmitter.emitExternally(2)

        eventEmitter.emitExternally(3)

        eventEmitter.emitExternally(5)

        eventEmitter.emitExternally(4)

        assertEquals(
            expected = listOf("2", "4"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(3)

        eventEmitter.emitExternally(5)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

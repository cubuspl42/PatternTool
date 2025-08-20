package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMapTests {
    @Test
    fun testMap() {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val mappedStream = eventEmitter.map { "$it" }

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = mappedStream,
        )

        eventEmitter.emitExternally(1)

        assertEquals(
            expected = listOf("1"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(2)

        assertEquals(
            expected = listOf("2"),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

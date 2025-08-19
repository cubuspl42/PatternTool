package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventEmitterTests {
    @Test
    fun testEventEmitter() {
        val eventEmitter = EventEmitter.createExternally<String>()

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = eventEmitter,
        )

        eventEmitter.emitExternally("Hello")
        eventEmitter.emitExternally("World")

        assertEquals(
            expected = listOf(
                "Hello",
                "World",
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally("Bye")

        assertEquals(
            expected = listOf(
                "Bye",
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

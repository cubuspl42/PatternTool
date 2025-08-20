package dev.toolkt.reactive.future

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureDivertHoldTests {
    @Test
    fun testDivertHold() {
        val eventEmitter1 = EventEmitter.createExternally<Int>()
        val eventEmitter2 = EventEmitter.createExternally<Int>()

        val futureCompleter = FutureCompleter.createExternally<EventStream<Int>>()

        val divertHoldStream = futureCompleter.divertHold(
            initialEventStream = eventEmitter1,
        )

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = divertHoldStream,
        )

        eventEmitter1.emitExternally(-11)
        eventEmitter2.emitExternally(11)

        assertEquals(
            expected = listOf(-11),
            actual = streamVerifier.removeReceivedEvents(),
        )

        futureCompleter.completeExternally(eventEmitter2)

        eventEmitter1.emitExternally(-12)
        eventEmitter2.emitExternally(12)

        assertEquals(
            expected = listOf(12),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

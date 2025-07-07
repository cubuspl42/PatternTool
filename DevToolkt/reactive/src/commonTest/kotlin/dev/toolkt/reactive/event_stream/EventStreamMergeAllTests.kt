package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMergeAllTests {
    @Test
    fun testMergeAll() {
        val eventEmitter0 = EventEmitter<Int>()
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val mergeStream = EventStream.mergeAll(
            eventEmitter0,
            eventEmitter1,
            eventEmitter2,
        )

        val streamVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        eventEmitter0.emit(10)
        eventEmitter1.emit(20)
        eventEmitter2.emit(30)

        assertEquals(
            expected = listOf(
                10,
                20,
                30,
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

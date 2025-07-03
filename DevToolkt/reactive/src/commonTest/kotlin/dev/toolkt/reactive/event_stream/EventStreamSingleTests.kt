package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.ensureNotCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class EventStreamSingleTests {
    @Test
    fun testSingle() {
        val eventEmitter = EventEmitter<Int>()

        val nextStream = eventEmitter.single()

        val streamVerifier = EventStreamVerifier(
            eventStream = nextStream,
        )

        // Emit the single event
        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )

        // Emit some event after the single event
        eventEmitter.emit(20)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        // Emit yet another event (just to be sure)
        eventEmitter.emit(30)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testSingle_keepAlive() = runTestDefault(
        timeout = 10.seconds,
    ) {
        val eventEmitter = EventEmitter<Int>()

        fun setup(): Pair<PlatformWeakReference<EventStream<Int>>, EventStreamVerifier<Int>> {
            val singleEventStream = eventEmitter.single()

            val streamVerifier = EventStreamVerifier(
                eventStream = singleEventStream,
            )

            return Pair(
                PlatformWeakReference(singleEventStream),
                streamVerifier,
            )
        }

        val (singleEventStreamWeakRef, streamVerifier) = setup()

        ensureNotCollected(weakRef = singleEventStreamWeakRef)

        // Emit the single event
        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import dev.toolkt.reactive.future.Future
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class EventStreamNextTests {
    @Test
    fun testNext() {
        val eventEmitter = EventEmitter<Int>()

        val nextFuture = eventEmitter.next()

        val onResultVerifier = EventStreamVerifier(
            eventStream = nextFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = nextFuture.currentState,
        )

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = nextFuture.currentState,
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = nextFuture.currentState,
        )
    }

    @Test
    fun testNext_garbageCollection() = runTestDefault(
        timeout = 10.seconds,
    ) {
        // This test might not make much sense

        val eventEmitter = EventEmitter<Int>()

        fun setup(): Pair<PlatformWeakReference<Future<Int>>, EventStreamVerifier<Int>> {
            val nextFuture = eventEmitter.next()

            val streamVerifier = EventStreamVerifier(
                eventStream = nextFuture.onResult,
            )

            return Pair(
                PlatformWeakReference(nextFuture),
                streamVerifier,
            )
        }

        val (outFutureWeakRef, onResultVerifier) = setup()

        // Emit the single event
        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }
}

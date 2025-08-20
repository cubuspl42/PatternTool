package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class EventStreamNextTests {
    @Test
    fun testNext() {
        val eventEmitter = EventEmitter<Int>()

        val nextFuture = eventEmitter.nextExternally()

        val onResultVerifier = EventStreamVerifier.setup(
            eventStream = nextFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = nextFuture.currentStateUnmanaged,
        )

        eventEmitter.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = nextFuture.currentStateUnmanaged,
        )

        eventEmitter.emitExternally(20)

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = nextFuture.currentStateUnmanaged,
        )
    }

    @Test
    fun testNext_garbageCollection() = runTestDefault(
        timeout = 10.seconds,
    ) {
        // This test might not make much sense

        val eventEmitter = EventEmitter<Int>()

        fun setup(): Pair<PlatformWeakReference<Future<Int>>, EventStreamVerifier<Int>> {
            val nextFuture = eventEmitter.nextExternally()

            val streamVerifier = EventStreamVerifier.setup(
                eventStream = nextFuture.onResult,
            )

            return Pair(
                PlatformWeakReference(nextFuture),
                streamVerifier,
            )
        }

        val (outFutureWeakRef, onResultVerifier) = setup()

        // Emit the single event
        eventEmitter.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }
}

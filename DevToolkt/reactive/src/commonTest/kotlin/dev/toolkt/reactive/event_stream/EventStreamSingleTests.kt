package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.collectGarbageSuspend
import dev.toolkt.core.platform.test_utils.ensureCollected
import dev.toolkt.core.platform.test_utils.ensureNotCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.test_utils.DetachedEventStreamVerifier
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun testSingle_keepAlive() = runTestDefault {
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

    @Test
    fun testSingle_letItGo() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val singleEventStreamWeakRef = PlatformWeakReference(eventEmitter.single())

        eventEmitter.emit(10)

        ensureCollected(weakRef = singleEventStreamWeakRef)
    }

    @Test
    fun testSingle_missed() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val singleEventStream = eventEmitter.single()

        eventEmitter.emit(10)

        val streamVerifier = DetachedEventStreamVerifier(
            eventStream = singleEventStream,
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testSingle_detached() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val streamVerifier = DetachedEventStreamVerifier(
            eventStream = eventEmitter.single(),
        )

        PlatformSystem.collectGarbageSuspend()

        // Emit the single event
        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

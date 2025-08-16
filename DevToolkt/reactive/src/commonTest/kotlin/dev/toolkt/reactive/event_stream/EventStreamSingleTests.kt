package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.ensureCollected
import dev.toolkt.core.platform.test_utils.ensureNotCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.test_utils.DetachedEventStreamVerifier
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class EventStreamSingleTests {
    @Test
    fun testSingle() {
        val eventEmitter = EventEmitter<Int>()

        val nextStream = eventEmitter.singleUnmanaged()

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
            val singleEventStream = eventEmitter.singleUnmanaged()

            val streamVerifier = EventStreamVerifier(
                eventStream = singleEventStream,
            )

            return Pair(
                PlatformWeakReference(singleEventStream),
                streamVerifier,
            )
        }

        val (singleEventStreamRef, streamVerifier) = setup()

        ensureNotCollected(weakRef = singleEventStreamRef)

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

        val singleEventStreamRef = PlatformWeakReference(eventEmitter.singleUnmanaged())

        eventEmitter.emit(10)

        PlatformSystem.collectGarbageForced()

        ensureCollected(weakRef = singleEventStreamRef)

        assertFalse(
            actual = eventEmitter.hasListeners,
        )
    }

    @Test
    fun testSingle_letItGo_noEmit() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val singleEventStreamRef = PlatformWeakReference(eventEmitter.singleUnmanaged())

        PlatformSystem.collectGarbageForced()

        ensureCollected(weakRef = singleEventStreamRef)

        assertFalse(
            actual = eventEmitter.hasListeners,
        )
    }

    @Test
    fun testSingle_missed() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val singleEventStream = eventEmitter.singleUnmanaged()

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
            eventStream = eventEmitter.singleUnmanaged(),
        )

        PlatformSystem.collectGarbageForced()

        // Emit the single event
        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

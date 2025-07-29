package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.collectGarbageSuspend
import dev.toolkt.core.platform.test_utils.ensureCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.test_utils.DetachedEventStreamVerifier
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class EventStreamTakeTests {
    @Test
    fun testTake_negative() {
        assertIs<IllegalArgumentException>(
            assertFails {
                NeverEventStream.take(-1)
            },
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                EventEmitter<Int>().take(-2)
            },
        )
    }

    @Test
    fun testTake_zero() {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = eventEmitter.take(0)

        val streamVerifier = EventStreamVerifier(
            eventStream = takeStream,
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testTake_one() {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = eventEmitter.take(1)

        val changesVerifier = EventStreamVerifier(
            eventStream = takeStream,
        )

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(30)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testTake_two() {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = eventEmitter.take(2)

        val changesVerifier = EventStreamVerifier(
            eventStream = takeStream,
        )

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = listOf(20),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(30)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testTake_letItGo() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val EventStreamRef = PlatformWeakReference(
            eventEmitter.take(2),
        )

        ensureCollected(EventStreamRef)
    }

    @Test
    fun testTake_missed() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = eventEmitter.take(2)

        eventEmitter.emit(10)
        eventEmitter.emit(20)

        val streamVerifier = EventStreamVerifier(
            eventStream = takeStream,
        )

        eventEmitter.emit(30)
        eventEmitter.emit(40)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }


    @Test
    fun testTake_detached() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val streamVerifier = DetachedEventStreamVerifier(
            eventStream = eventEmitter.take(2),
        )

        PlatformSystem.collectGarbageSuspend()

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = listOf(20),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(30)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.ensureCollected
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.managed_io.Moments
import dev.toolkt.reactive.test_utils.DetachedEventStreamVerifier
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class EventStreamTakeTests {
    @Test
    fun testTake_negative() {
        assertIs<IllegalArgumentException>(
            assertFails {
                Moments.external {
                    NeverEventStream.take(-1)
                }
            },
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                Moments.external {
                    EventEmitter<Int>().take(-2)
                }
            },
        )
    }

    @Test
    fun testTake_zero() {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = Moments.external {
            eventEmitter.take(0)
        }

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = takeStream,
        )

        eventEmitter.emitExternally(1)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(2)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testTake_one() {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = Moments.external {
            eventEmitter.take(1)
        }

        val changesVerifier = EventStreamVerifier.setup(
            eventStream = takeStream,
        )

        eventEmitter.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(20)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(30)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testTake_two() {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = Moments.external {
            eventEmitter.take(2)
        }

        val changesVerifier = EventStreamVerifier.setup(
            eventStream = takeStream,
        )

        eventEmitter.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(20)

        assertEquals(
            expected = listOf(20),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(30)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testTake_collectable() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val takeStreamRef = PlatformWeakReference(
            Moments.external {
                eventEmitter.take(3)
            },
        )


        ensureCollected(takeStreamRef)
    }

    @Test
    fun testTake_missed() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val takeStream = Moments.external {
            eventEmitter.take(2)
        }

        eventEmitter.emitExternally(10)
        eventEmitter.emitExternally(20)

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = takeStream,
        )

        eventEmitter.emitExternally(30)
        eventEmitter.emitExternally(40)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }


    @Test
    fun testTake_detached() = runTestDefault {
        val eventEmitter = EventEmitter<Int>()

        val streamVerifier = DetachedEventStreamVerifier(
            eventStream = Moments.external {
                eventEmitter.take(2)
            },
        )

        PlatformSystem.collectGarbageForced()

        eventEmitter.emitExternally(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(20)

        assertEquals(
            expected = listOf(20),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(30)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

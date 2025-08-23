package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.event_stream.single
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventStreamSingleTests {
    @Test
    fun testSingle_caught() = runTestDefault {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val (singleEventStreamWeakRef, streamVerifier) = Actions.external {
            // Create the single stream without storing a direct reference,
            eventEmitter.single()
        }.let { singleEventStream ->
            Pair(
                // Store only a weak reference, to make sure that we don't "help" tbe
                // single stream object in the aspect of being non-collectable.
                // We'll still have an indirect reference via the subscription, which
                // should keep the single stream alive if it's implemented correctly.
                PlatformWeakReference(singleEventStream),
                // Start listening before the single event is emitted
                EventStreamVerifier.setup(eventStream = singleEventStream),
            )
        }

        // Verify that the single event stream subscribed to the source stream
        assertTrue(
            actual = eventEmitter.hasListeners,
        )

        // Emit the single event from the source stream
        eventEmitter.emitExternally(10)

        // Verify that the single event stream unsubscribed from the source stream
        // after the single event was emitted
        assertFalse(
            actual = eventEmitter.hasListeners,
        )

        // Verify that the single event was received
        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )

        // Emit another event from the source stream
        eventEmitter.emitExternally(20)

        // Verify that the new event was ignored
        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        // Force garbage collection
        PlatformSystem.collectGarbageForced()

        // Verify that the single event stream allowed itself to be collected
        // after it emitted the single event, even though we still hold an indirect
        // reference to it via the subscription.

        // TODO: Is this easy/reasonable to implement?
        /*

        assertNull(
            actual = singleEventStreamWeakRef.get(),
        )

         */
    }

    @Test
    fun testSingle_missed() = runTestDefault {
        val eventEmitter = EventEmitter.createExternally<Int>()


        val (singleEventStreamWeakRef, streamVerifier) = Actions.external {
            eventEmitter.single()
        }.let { singleEventStream ->
            // Verify that the single event stream subscribed to the source stream,
            // proving that it's active even without any listeners
            assertTrue(
                actual = eventEmitter.hasListeners,
            )

            // Emit the single event (it is missed, as there are no listeners yet)
            eventEmitter.emitExternally(10)

            // Verify that the single event stream unsubscribed from the source stream
            // after the single event was emitted (even without listeners)
            assertFalse(
                actual = eventEmitter.hasListeners,
            )

            Pair(
                // Store only a weak reference, to make sure that we don't "help" tbe
                // single stream object in the aspect of being non-collectable.
                // We'll still have an indirect reference via the subscription, which
                // should keep the single stream alive if it's implemented correctly.
                PlatformWeakReference(singleEventStream),
                // Start listening before the single event is emitted
                EventStreamVerifier.setup(eventStream = singleEventStream),
            )
        }

        // Verify that the source stream still has no listeners, even after we
        // started listening to it, proving that the single stream is aware that
        // the single event was already emitted.
        assertFalse(
            actual = eventEmitter.hasListeners,
        )

        // Emit another event from the source stream
        eventEmitter.emitExternally(20)

        // Verify that no events were received, proving that the single event
        // stream managed internal state even before we started listening and
        // is not going to emit any events after the single one
        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        PlatformSystem.collectGarbageForced()

        // Verify that the single event stream allowed itself to be collected
        // after it emitted the single event, even though we still hold an indirect
        // reference to it via the subscription.

        // TODO: This might be a bad idea; instead, the single stream object SHOULD NOT create a subscription if
        //  it's already in the terminated state
        /*

        assertNull(
            actual = singleEventStreamWeakRef.get(),
        )

         */
    }
}

package dev.toolkt.reactive.event_stream

import dev.toolkt.core.async_tests.AsyncTest
import dev.toolkt.core.async_tests.AsyncTestGroup
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.awaitCollection
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.Moments
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

data object EventStreamSingleGarbageCollectionTestGroup : AsyncTestGroup() {
    override val tests = listOf(
        NonHappeningTest,
        HappeningTest,
    )

    data object NonHappeningTest : AsyncTest() {
        override suspend fun execute() {
            // Create an event emitter that will not emit any events
            val eventEmitter = EventEmitter.createExternally<Int>()

            // Create a single event stream which we'll never listen to.
            val nonHappeningSingleEventStreamWeakRef = Actions.external {
                eventEmitter.single()
            }.let { nonHappeningSingleEventStream ->
                // Store only a weak reference, so it's a candidate for garbage collection (as we don't listen either).
                // It's not possible for the single stream object to be aware of this!
                PlatformWeakReference(nonHappeningSingleEventStream)
            }

            // Verify that the single event stream subscribed to the source stream
            // (the garbage collection couldn't have happened yet)
            assertTrue(
                actual = eventEmitter.hasListeners,
            )

            nonHappeningSingleEventStreamWeakRef.awaitCollection(
                tag = "EventStreamSingleTest/nonHappeningSingleEventStream",
            )

            // Verify that the single event stream allowed itself to be collected,
            // even though it was managing some internal state while waiting for
            // the single source event and its first listener
            assertNull(
                actual = nonHappeningSingleEventStreamWeakRef.get(),
            )

            // Verify that the single event unsubscribed from the source stream
            // as a part of the cleanup process
            assertFalse(
                actual = eventEmitter.hasListeners,
            )
        }
    }

    data object HappeningTest : AsyncTest() {
        override suspend fun execute() {
            // Create an event emitter that will emit some event(s)
            val eventEmitter = EventEmitter.createExternally<Int>()

            // Create a mapped event stream that will be the source
            val mappedEventStreamWeakRef = eventEmitter.map {
                it.toString()
            }.let { mappedEventStream ->
                // Create a single event stream which we'll listen to
                Moments.external {
                    mappedEventStream.single()
                }.apply {
                    listenExternally { }
                }

                // Create a single event stream which we won't listen to
                Moments.external {
                    mappedEventStream.single()
                }

                PlatformWeakReference(mappedEventStream)
            }

            assertTrue(
                actual = eventEmitter.hasListeners,
            )

            eventEmitter.emitExternally(1)

            /* TODO: Implement this behavior:

            // Verify that the single event stream dropped all references to its
            // source after the single event occurred
            mappedEventStreamWeakRef.awaitCollection(
                tag = "EventStreamSingleTest/mappedEventStream",
            )

            assertNull(
                actual = mappedEventStreamWeakRef.get(),
            )

            assertFalse(
                actual = loudEventEmitter.hasListeners,
            )

             */
        }
    }
}

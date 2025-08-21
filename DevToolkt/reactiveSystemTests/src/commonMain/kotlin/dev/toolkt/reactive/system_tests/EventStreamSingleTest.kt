package dev.toolkt.reactive.system_tests

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.system_tests.utils.assertFalse
import dev.toolkt.reactive.system_tests.utils.assertNull
import dev.toolkt.reactive.system_tests.utils.assertTrue
import dev.toolkt.reactive.system_tests.utils.awaitCollection

data object EventStreamSingleTest : ReactiveSystemTest {
    override suspend fun execute() {
        // Create an event emitter that will not emit any events
        val eventEmitter = EventEmitter.createExternally<Int>()

        val singleEventStreamWeakRef = Actions.external {
            // Create a single event stream which we'll never listen to.
            eventEmitter.single()
        }.let {
            // Store only a weak reference, so it's a candidate for garbage collection (as we don't listen either).
            // It's not possible for the single stream object to be aware of this!
            PlatformWeakReference(it)
        }

        // Verify that the single event stream subscribed to the source stream
        // (the garbage collection couldn't have happened yet)
        assertTrue(
            actual = eventEmitter.hasListeners,
        )

        singleEventStreamWeakRef.awaitCollection()

        // Verify that the single event stream allowed itself to be collected,
        // even though it was managing some internal state while waiting for
        // the first listener
        assertNull(
            actual = singleEventStreamWeakRef.get(),
        )

        // Verify that the single event unsubscribed from the source stream
        // as a part of the cleanup process
        assertFalse(
            actual = eventEmitter.hasListeners,
        )
    }
}

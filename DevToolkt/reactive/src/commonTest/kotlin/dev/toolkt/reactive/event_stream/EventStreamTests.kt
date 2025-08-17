package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

/**
 * Tests of the fundamental [EventStream] operations
 */
class EventStreamTests {
    @Test
    fun testListen() {
        val eventEmitter = EventEmitter<Int>()

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        val buffer = mutableListOf<String>()

        fun add(element: String) {
            buffer.add(element)
        }

        val subscription = dependentStream.listenExternally(::add)

        assertEquals(
            expected = emptyList(),
            actual = buffer,
        )

        eventEmitter.emitUnmanaged(1)

        assertEquals(
            expected = listOf("1"),
            actual = buffer,
        )

        eventEmitter.emitUnmanaged(2)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer,
        )

        subscription.cancel()

        eventEmitter.emitUnmanaged(3)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer,
        )
    }

    @Test
    fun testListenTwice() {
        val listener = object : Listener<Any> {
            override fun handle(event: Any) {
            }
        }

        val eventEmitter = EventEmitter<Int>()

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        dependentStream.listen(listener)

        assertIs<IllegalStateException>(
            assertFails {
                dependentStream.listen(listener)
            })
    }

    private class Buffer {
        val list = mutableListOf<String>()
    }

    @Test
    fun testListenWeak() {
        val eventEmitter = EventEmitter<Int>()

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        val buffer = Buffer()

        fun add(
            buffer: Buffer,
            element: String,
        ) {
            buffer.list.add(element)
        }

        val subscription = dependentStream.listenWeak(
            target = buffer, listener = ::add
        )

        assertEquals(
            expected = emptyList(),
            actual = buffer.list,
        )

        eventEmitter.emitUnmanaged(1)

        assertEquals(
            expected = listOf("1"),
            actual = buffer.list,
        )

        eventEmitter.emitUnmanaged(2)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer.list,
        )

        subscription.cancel()

        eventEmitter.emitUnmanaged(3)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer.list,
        )
    }

    @Test
    fun testListenWeak_sameListener_sameTarget() {
        fun test(
            weakListener: TargetingListener<Any, String>,
        ) {
            val eventEmitter = EventEmitter<Int>()

            val target = object {}

            // Create a dependent stream to make the system non-trivial
            val dependentStream = eventEmitter.map { it.toString() }

            dependentStream.listenWeak(
                target = target,
                listener = weakListener,
            )

            dependentStream.listenWeak(
                target = target,
                listener = weakListener,
            )
        }

        // As the ::function operator doesn't return stable references on
        // JavaScript, we ensure that the weak listener is bound to a function
        // argument
        test(
            weakListener = object : TargetingListener<Any, Any> {
                override fun handle(
                    target: Any,
                    event: Any,
                ) {
                }
            },
        )
    }


    @Test
    fun testListenWeak_differentListeners_sameTarget() {
        val eventEmitter = EventEmitter<Int>()

        val target = object {}

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        @Suppress("unused")
        fun handle1(target: Any, element: String) {
        }

        @Suppress("unused")
        fun handle2(target: Any, element: String) {
        }

        dependentStream.listenWeak(
            target = target,
            listener = ::handle1,
        )

        dependentStream.listenWeak(
            target = target,
            listener = ::handle2,
        )
    }

    @Test
    fun testListenWeak_sameListener_differentTargets() {
        val eventEmitter = EventEmitter<Int>()

        val target1 = object {}

        val target2 = object {}

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        @Suppress("unused")
        fun handle(target: Any, element: String) {
        }

        dependentStream.listenWeak(
            target = target1,
            listener = ::handle,
        )

        dependentStream.listenWeak(
            target = target2,
            listener = ::handle,
        )
    }
}

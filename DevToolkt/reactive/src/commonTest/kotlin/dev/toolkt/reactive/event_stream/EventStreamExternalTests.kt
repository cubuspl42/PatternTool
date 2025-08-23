package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.endExternally
import dev.toolkt.reactive.effect.startExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamExternalTests {
    private class ExternalSource<E> {
        private val listeners = mutableListOf<(E) -> Unit>()

        fun addListener(
            listener: (E) -> Unit,
        ) {
            listeners.add(listener)
        }

        fun removeListener(
            listener: (E) -> Unit,
        ) {
            listeners.remove(listener)
        }

        fun emit(value: E) {
            listeners.forEach { it(value) }
        }
    }

    @Test
    fun testSubscribeExternal() {
        val externalSource = ExternalSource<Int>()

        val externalEventStream = EventStream.subscribeExternal { controller ->
            val listener = fun(event: Int) {
                controller.accept(event = event)
            }

            object : EventStream.ExternalSubscription {
                override fun register() {
                    externalSource.addListener(
                        listener = listener,
                    )

                }

                override fun unregister() {
                    externalSource.removeListener(
                        listener = listener,
                    )
                }
            }
        }

        externalSource.emit(1)

        val streamVerifier = EventStreamVerifier.listenForever(
            eventStream = externalEventStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        externalSource.emit(1)

        assertEquals(
            expected = listOf(1),
            actual = streamVerifier.removeReceivedEvents(),
        )

        externalSource.emit(2)

        assertEquals(
            expected = listOf(2),
            actual = streamVerifier.removeReceivedEvents(),
        )

    }

    @Test
    fun testActivateExternal() {
        val externalSource = ExternalSource<Int>()

        val externalEventStreamEffect = Actions.external {
            EventStream.activateExternal { controller ->
                val listener = fun(event: Int) {
                    controller.accept(event = event)
                }

                externalSource.addListener(
                    listener = listener,
                )

                object : Subscription {
                    override fun cancel() {
                        externalSource.removeListener(
                            listener = listener,
                        )
                    }
                }
            }
        }

        val (externalEventStream, handle) = externalEventStreamEffect.startExternally()

        externalSource.emit(1)

        val streamVerifier = EventStreamVerifier.listenForever(
            eventStream = externalEventStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        externalSource.emit(1)

        assertEquals(
            expected = listOf(1),
            actual = streamVerifier.removeReceivedEvents(),
        )

        externalSource.emit(2)

        assertEquals(
            expected = listOf(2),
            actual = streamVerifier.removeReceivedEvents(),
        )

        handle.endExternally()

        externalSource.emit(3)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}

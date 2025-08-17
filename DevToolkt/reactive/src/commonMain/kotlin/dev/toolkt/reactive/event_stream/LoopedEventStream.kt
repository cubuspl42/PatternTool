package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

class LoopedEventStream<E>() : ProperEventStream<E>() {
    sealed class PlaceholderSubscription<E> : Subscription {
        abstract fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        )
    }

    class PlaceholderStrongSubscription<E>(
        initialBufferedListener: Listener<E>,
    ) : PlaceholderSubscription<E>() {
        class BufferedSubscription<E>(
            var bufferedListener: Listener<E>?,
        ) : Subscription {
            fun loop(
                eventStream: EventStream<E>,
                initialEvent: E?,
            ): Subscription? {
                val bufferedListener = this.bufferedListener ?: return null

                if (initialEvent != null) {
                    bufferedListener.handle(
                        transaction = TODO("Make loops transaction-aware"),
                        event = initialEvent,
                    )
                }

                return eventStream.listen(listener = bufferedListener)
            }

            override fun cancel() {
                if (bufferedListener == null) {
                    throw IllegalStateException("The subscription is already cancelled")
                }

                bufferedListener = null
            }
        }

        private var innerSubscription: Subscription? = BufferedSubscription(
            bufferedListener = initialBufferedListener,
        )

        override fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        ) {
            @Suppress("UNCHECKED_CAST") val bufferedSubscription = innerSubscription as? BufferedSubscription<E>
                ?: throw IllegalStateException("The subscription is already looped")

            innerSubscription = bufferedSubscription.loop(
                eventStream = eventStream,
                initialEvent = initialEvent,
            )
        }

        override fun cancel() {
            val innerSubscription =
                this.innerSubscription ?: throw IllegalStateException("The subscription is already cancelled")

            innerSubscription.cancel()
        }
    }

    class PlaceholderEventStream<E> : ProperEventStream<E>() {
        private val placeholderSubscriptions = mutableSetOf<PlaceholderSubscription<E>>()

        override fun listen(listener: Listener<E>): Subscription {
            val placeholderSubscription = PlaceholderStrongSubscription(initialBufferedListener = listener)

            placeholderSubscriptions.add(placeholderSubscription)

            return placeholderSubscription
        }

        fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        ) {
            placeholderSubscriptions.forEach { subscription ->
                subscription.loop(
                    eventStream = eventStream,
                    initialEvent = initialEvent,
                )
            }
        }
    }

    private var innerEventStream: EventStream<E> = PlaceholderEventStream()

    fun loop(
        eventStream: EventStream<E>,
        initialEvent: E? = null,
    ) {
        val placeholderEventStream = this.innerEventStream as? PlaceholderEventStream<E> ?: throw IllegalStateException(
            "The stream is already looped"
        )

        placeholderEventStream.loop(
            eventStream = eventStream,
            initialEvent = initialEvent,
        )

        innerEventStream = eventStream
    }

    override fun listen(
        listener: Listener<E>,
    ): Subscription = innerEventStream.listen(
        listener = listener,
    )
}

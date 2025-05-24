package diy.lingerie.frp

import diy.lingerie.frp.NotifyingStream.ListenerStrength

abstract class EventStream<out E> {
    companion object {
        private val finalizationRegistry = PlatformFinalizationRegistry()

        val Never: EventStream<Nothing> = NeverEventStream

        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> = DivertEventStream(
            nestedEventStream = nestedEventStream,
        )
    }

    abstract fun subscribe(
        listener: Listener<E>,
        strength: ListenerStrength = ListenerStrength.Strong,
    ): Subscription

    fun subscribeSemiBound(
        target: Any,
        listener: Listener<E>,
    ): Subscription {
        val cleanable = subscribeBound(
            target,
            listener,
        )

        return object : Subscription {
            override fun cancel() {
                cleanable.clean()
            }
        }
    }

    fun subscribeFullyBound(
        target: Any,
        listener: Listener<E>,
    ) {
        // Ignore the cleanable, depend on the finalization register only

        subscribeBound(
            target = target,
            listener = listener,
        )
    }

    private fun subscribeBound(
        target: Any,
        listener: Listener<E>,
    ): PlatformCleanable {
        val weakSubscription = subscribe(
            listener = listener,
            strength = ListenerStrength.Weak,
        )

        return finalizationRegistry.register(
            target = target,
        ) {
            weakSubscription.cancel()
        }
    }

    fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = MapEventStream(
        source = this,
        transform = transform,
    )

    fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = FilterEventStream(
        source = this,
        predicate = predicate,
    )
}

fun <E> EventStream<E>.hold(
    initialValue: E,
): Cell<E> = HoldCell(
    values = this,
    initialValue = initialValue,
)

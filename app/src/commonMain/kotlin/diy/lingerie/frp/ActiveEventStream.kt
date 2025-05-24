package diy.lingerie.frp

private val finalizationRegistry = PlatformFinalizationRegistry()

abstract class ActiveEventStream<E>() : EventStream<E>() {
    final override fun subscribe(
        listener: Listener<E>, strength: Notifier.ListenerStrength
    ): Subscription = vertex.subscribe(
        listener = listener,
        strength = strength,
    )

    final override fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = DependentEventStream(
        vertex = MapEventStreamVertex(
            source = this,
            transform = transform,
        ),
    )

    final override fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = DependentEventStream(
        vertex = FilterEventStreamVertex(
            source = this,
            predicate = predicate,
        ),
    )

    final override fun <T : Any> pipe(
        target: T,
        consume: (E) -> Unit,
    ) {
        val subscription = vertex.subscribe(
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    consume(event)
                }
            },
        )

        finalizationRegistry.register(
            target = target,
        ) {
            subscription.cancel()
        }
    }

    internal abstract val vertex: Vertex<E>
}

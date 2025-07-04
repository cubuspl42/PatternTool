package dev.toolkt.reactive.event_stream

class SingleEventStream<E>(
    private val source: EventStream<E>,
) : StatefulEventStream<E>() {
    private var wasEmitted = false

    override fun bind(): BoundListener = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<SingleEventStream<E>, E> {
            override fun handle(
                target: SingleEventStream<E>,
                event: E,
            ) {
                if (target.wasEmitted) {
                    // Abortion failed (?)
                    throw AssertionError("The single event was already emitted")
                }

                target.notify(event = event)

                target.wasEmitted = true

                target.abort()
            }
        },
        target = this,
    )

    init {
        init()
    }
}

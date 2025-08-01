package dev.toolkt.reactive.event_stream

class SingleEventStream<EventT>(
    private val source: EventStream<EventT>,
) : StatefulEventStream<SingleEventStream<EventT>, EventT>() {
    private var wasEmitted = false

    override fun bind() = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<SingleEventStream<EventT>, EventT> {
            override fun handle(
                target: SingleEventStream<EventT>,
                event: EventT,
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
    )

    init {
        init(target = this)
    }
}

package dev.toolkt.reactive.event_stream

internal class TakeUntilNullStream<EventT : Any>(
    private val source: EventStream<EventT?>,
) : StatefulEventStream<TakeUntilNullStream<EventT>, EventT>() {
    override fun bind() = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<TakeUntilNullStream<EventT>, EventT?> {
            override fun handle(
                target: TakeUntilNullStream<EventT>,
                event: EventT?,
            ) {
                when (event) {
                    null -> {
                        target.abort()
                    }

                    else -> {
                        target.notify(event = event)
                    }
                }
            }
        },
    )

    init {
        init(target = this)
    }
}

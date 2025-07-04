package dev.toolkt.reactive.event_stream

class TakeEventStream<E>(
    private val source: EventStream<E>,
    totalCount: Int,
) : StatefulEventStream<E>() {
    private var remainingCount: Int = totalCount

    override fun bind(): BoundListener = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<TakeEventStream<E>, E> {
            override fun handle(
                target: TakeEventStream<E>,
                event: E,
            ) {
                val oldRemainingCount = target.remainingCount

                if (oldRemainingCount <= 0) {
                    // Abortion failed (?)
                    throw AssertionError("No more remaining events to take")
                }

                val newRemainingCount = oldRemainingCount - 1
                target.remainingCount = newRemainingCount

                target.notify(event = event)

                if (newRemainingCount == 0) {
                    target.abort()
                }
            }
        },
        target = this,
    )

    init {
        init()
    }
}

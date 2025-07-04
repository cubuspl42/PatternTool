package dev.toolkt.reactive.event_stream

class TakeEventStream<EventT>(
    private val source: EventStream<EventT>,
    totalCount: Int,
) : StatefulEventStream<TakeEventStream<EventT>, EventT>() {
    private var remainingCount: Int = totalCount

    override fun bind(): SourcedListener<TakeEventStream<EventT>, EventT> = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<TakeEventStream<EventT>, EventT> {
            override fun handle(
                target: TakeEventStream<EventT>,
                event: EventT,
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
    )

    init {
        init(target = this)
    }
}

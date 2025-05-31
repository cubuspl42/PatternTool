package dev.toolkt.reactive.event_stream

class LoopedEventStream<E>() : ProperEventStream<E>() {
    private var loopedEventStream: EventStream<E>? = null

    fun loop(
        eventStream: EventStream<E>,
        initialEvent: E? = null,
    ) {
        if (this.loopedEventStream != null) {
            throw IllegalStateException("The stream is already looped")
        }

        eventStream.internalizeIfProper(this)

        loopedEventStream = eventStream
    }

    override fun register(
        eventHandler: EventHandler<E>,
        strength: SubscriptionSet.SubscriptionStrength
    ): LinkedSubscription<E>? {
        TODO()
    }

    override fun unregister(
        eventHandler: EventHandler<E>,
        strength: SubscriptionSet.SubscriptionStrength
    ): LinkedSubscription<E> {
        TODO()
    }

    override fun onResumed() {
        TODO("Not yet implemented")
    }

    override fun onPaused() {
        TODO("Not yet implemented")
    }

    override val successorEventStream: EventStream<E>?
        get() = loopedEventStream
}

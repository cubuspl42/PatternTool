package diy.lingerie.frp

internal object NeverEventStream : EventStream<Nothing>() {
    override fun subscribe(
        listener: Listener<Nothing>, strength: Notifier.ListenerStrength
    ): Subscription = Subscription.Noop

    override fun <Er> map(
        transform: (Nothing) -> Er,
    ): EventStream<Er> = NeverEventStream

    override fun filter(
        predicate: (Nothing) -> Boolean,
    ): EventStream<Nothing> = NeverEventStream

    override fun <T : Any> pipe(
        target: T,
        consume: (Nothing) -> Unit,
    ) {
    }
}

package diy.lingerie.frp

internal object NeverEventStream : EventStream<Nothing>() {
    override fun subscribe(
        listener: Listener<Nothing>,
        strength: Notifier.ListenerStrength
    ): Subscription = Subscription.Noop
}

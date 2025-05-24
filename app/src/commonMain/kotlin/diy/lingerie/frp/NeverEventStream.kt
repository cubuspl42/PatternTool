package diy.lingerie.frp

internal object NeverEventStream : EventStream<Nothing>() {
    override fun subscribe(
        listener: Listener<Nothing>,
        strength: NotifyingStream.ListenerStrength
    ): Subscription = Subscription.Noop
}

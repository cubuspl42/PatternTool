package diy.lingerie.frp

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }

        override fun change(strength: Notifier.ListenerStrength) {
        }
    }

    fun cancel()

    fun change(strength: Notifier.ListenerStrength)
}

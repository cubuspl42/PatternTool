package diy.lingerie.frp

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }

        override fun change(strength: Vertex.ListenerStrength) {
        }
    }

    fun cancel()

    fun change(strength: Vertex.ListenerStrength)
}

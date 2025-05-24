package diy.lingerie.frp

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }
    }

    fun cancel()
}

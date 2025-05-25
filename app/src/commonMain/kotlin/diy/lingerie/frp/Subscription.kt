package diy.lingerie.frp

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }
    }

    fun cancel()
}

interface HybridSubscription : Subscription {
    object Noop : HybridSubscription {
        override fun cancel() {
        }

        override fun weaken() {
        }

        override fun strengthen() {
        }
    }

    fun weaken()

    fun strengthen()
}

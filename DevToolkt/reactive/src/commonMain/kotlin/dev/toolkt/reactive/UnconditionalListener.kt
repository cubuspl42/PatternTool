package dev.toolkt.reactive

import dev.toolkt.reactive.effect.Transaction

abstract class UnconditionalListener<in EventT> : Listener<EventT> {
    final override fun handle(
        transaction: Transaction,
        event: EventT,
    ): Listener.Conclusion {
        handleUnconditionally(
            transaction = transaction,
            event = event,
        )

        return Listener.Conclusion.KeepListening
    }

    abstract fun handleUnconditionally(
        transaction: Transaction,
        event: EventT,
    )
}

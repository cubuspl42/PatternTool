package dev.toolkt.reactive

import dev.toolkt.reactive.managed_io.Transaction

typealias ListenerFn<E> = (E) -> Unit

interface Listener<in EventT> {
    companion object {
        fun <TargetT : Any, EventT> wrap(
            fn: ListenerFn<EventT>,
        ): Listener<EventT> = object : Listener<EventT> {
            override fun handle(
                transaction: Transaction,
                event: EventT,
            ) {
                fn(event)
            }
        }
    }

    val dependentId: Int?
        get() = null

    /**
     * Handles the [event] within the [transaction].
     */
    fun handle(
        transaction: Transaction,
        event: EventT,
    )
}

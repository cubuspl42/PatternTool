package dev.toolkt.reactive

import dev.toolkt.reactive.effect.Transaction

typealias ListenerFn<E> = (E) -> Unit

interface Listener<in EventT> {
    enum class Conclusion {
        KeepListening, StopListening,
    }

    companion object;

    val dependentId: Int?
        get() = null

    /**
     * Handle the [event].
     *
     * @return Whether to keep listening or stop listening.
     *
     * This method is called during the propagation phase.
     */
    fun handle(
        transaction: Transaction,
        event: EventT,
    ): Conclusion
}

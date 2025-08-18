package dev.toolkt.reactive

import dev.toolkt.reactive.managed_io.Transaction

typealias ListenerFn<E> = (E) -> Unit

interface Listener<in EventT> {
    enum class Conclusion {
        KeepListening, StopListening,
    }

    companion object;

    val dependentId: Int?
        get() = null

    /**
     * Handles the [event] within the [transaction].
     */
    fun handle(
        transaction: Transaction,
        event: EventT,
    ): Conclusion
}

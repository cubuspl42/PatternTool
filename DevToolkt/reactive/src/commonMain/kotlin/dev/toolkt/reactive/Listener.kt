package dev.toolkt.reactive

typealias ListenerFn<E> = (E) -> Unit

interface Listener<in EventT> {
    companion object {
        fun <TargetT : Any, EventT> wrap(
            fn: ListenerFn<EventT>,
        ): Listener<EventT> = object : Listener<EventT> {
            override fun handle(
                event: EventT,
            ) {
                fn(event)
            }
        }
    }

    val dependentId: Int?
        get() = null

    /**
     * A function that accepts an event.
     */
    fun handle(
        event: EventT,
    )
}

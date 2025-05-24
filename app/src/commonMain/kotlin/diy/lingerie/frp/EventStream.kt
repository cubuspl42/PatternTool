package diy.lingerie.frp

abstract class EventStream<out E> {
    companion object {
        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> {
            TODO()
        }
    }

    private val listeners = mutableSetOf<Listener<E>>()

    fun subscribe(
        listener: Listener<E>,
    ): Subscription {
        listeners.add(listener)

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = listeners.remove(listener)

                if (!wasRemoved) {
                    throw AssertionError("Listener was not found")
                }
            }
        }
    }

    fun subscribeBound(
        listener: Listener<E>,
        target: Any,
    ) {
        TODO()
    }

    fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> {
        TODO()
    }

    fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> {
        TODO()
    }
}

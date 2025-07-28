package dev.toolkt.reactive.event_stream

import dev.toolkt.core.collections.mutableStableBagOf
import dev.toolkt.reactive.Listener

sealed class ListenerContainer<EventT> {
    interface Handle {
        fun remove()
    }

    abstract val listenerCount: Int

    abstract fun notifyAll(
        event: EventT,
    )

    abstract fun clear()
}

class StrongListenerContainer<EventT> : ListenerContainer<EventT>() {
    // The order of listeners invocation is non-deterministic (this could be
    // changed by using a linked mutable set, but that wouldn't give much
    // without reworking the weak listeners too)
    private val listeners = mutableStableBagOf<Listener<EventT>>()

    override val listenerCount: Int
        get() = listeners.size

    override fun notifyAll(event: EventT) {
        val listeners = listeners.toList()

        listeners.forEach { listener ->
            listener.handle(event)
        }
    }

    override fun clear() {
        listeners.clear()
    }

    fun insert(
        listener: Listener<EventT>,
    ): Handle {
        val handle = listeners.addEx(listener)

        return object : Handle {
            override fun remove() {
                listeners.removeVia(handle = handle)
            }
        }
    }
}


package dev.toolkt.reactive.event_stream

import dev.toolkt.core.collections.MutableAssociativeCollection
import dev.toolkt.core.collections.insertEffectively
import dev.toolkt.core.collections.insertEffectivelyWeak
import dev.toolkt.core.collections.mutableWeakMultiValuedMapOf
import dev.toolkt.core.collections.removeEffectively
import dev.toolkt.reactive.Listener

sealed class ListenerContainer<EventT> {
    interface Handle {
        fun remove()
    }

    abstract val listenerCount: Int

    abstract fun notifyAll(
        event: EventT,
    )

    abstract fun <TargetT : Any> insertTargeted(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Handle
}

class StrongListenerContainer<EventT> : ListenerContainer<EventT>() {
    // The order of listeners invocation is non-deterministic (this could be
    // changed by using a linked mutable set, but that wouldn't give much
    // without reworking the weak listeners too)
    private val listeners = mutableSetOf<Listener<EventT>>()

    override val listenerCount: Int
        get() = listeners.size

    override fun notifyAll(event: EventT) {
        listeners.forEach {
            it(event)
        }
    }

    override fun <TargetT : Any> insertTargeted(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Handle = insert {
        listener(target, it)
    }

    fun insert(
        listener: Listener<EventT>,
    ): Handle {
        val remover = listeners.insertEffectively(listener)

        return object : Handle {
            override fun remove() {
                remover.removeEffectively()
            }
        }
    }
}

class WeakListenerContainer<EventT> : ListenerContainer<EventT>() {
    // The order of weak listeners invocation is non-deterministic (changing
    // this would require a new multivalued map implementation)
    // TODO: Switch to `mutableStableWeakMultiValuedMapOf` and start using handles
    private val weakListeners: MutableAssociativeCollection<Any, TargetingListener<Any, EventT>> =
        mutableWeakMultiValuedMapOf()

    override val listenerCount: Int
        get() = weakListeners.size

    override fun notifyAll(event: EventT) {
        weakListeners.forEach { (target, weakListener) ->
            weakListener(target, event)
        }
    }

    override fun <TargetT : Any> insertTargeted(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Handle {
        val remover = weakListeners.insertEffectivelyWeak(
            key = target,
            value = @Suppress("UNCHECKED_CAST") (listener as TargetingListener<Any, EventT>),
        )

        return object : Handle {
            override fun remove() {
                // We don't check whether the entry was successfully removed,
                // as the entry might've been purged if the target was collected
                remover.remove()
            }
        }
    }
}

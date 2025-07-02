package dev.toolkt.reactive.event_stream

import dev.toolkt.core.collections.MutableStableBag
import dev.toolkt.core.collections.insertEffectively
import dev.toolkt.core.collections.mutableStableBagOf
import dev.toolkt.core.collections.removeEffectively
import dev.toolkt.core.platform.PlatformWeakReference
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
        val listeners = listeners.toList()

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
    interface ListenerEntry<EventT> {
        fun notify(event: EventT)
    }

    private data class WeakTargetedListener<TargetT : Any, EventT>(
        val weakTarget: PlatformWeakReference<TargetT>,
        val listener: TargetingListener<TargetT, EventT>,
    ) : ListenerEntry<EventT> {
        override fun notify(event: EventT) {
            val target = weakTarget.get() ?: return
            listener(target, event)
        }
    }

    // The order of weak listeners invocation is non-deterministic (changing
    // this would require a new multivalued map implementation)
    private val weakListeners: MutableStableBag<ListenerEntry<EventT>> = mutableStableBagOf<ListenerEntry<EventT>>()

    override val listenerCount: Int
        get() = weakListeners.size

    override fun notifyAll(event: EventT) {
        val weakListeners = weakListeners.toList()

        weakListeners.forEach {
            it.notify(event = event)
        }
    }

    override fun <TargetT : Any> insertTargeted(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Handle {
        val handle = weakListeners.addEx(
            WeakTargetedListener(
                weakTarget = PlatformWeakReference(target),
                listener = listener,
            ),
        )

        return object : Handle {
            override fun remove() {
                // We don't check whether the entry was successfully removed,
                // as the entry might've been purged if the target was collected
                weakListeners.removeVia(handle = handle)
            }
        }
    }
}

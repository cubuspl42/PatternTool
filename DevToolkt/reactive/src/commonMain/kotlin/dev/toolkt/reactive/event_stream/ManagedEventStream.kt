package dev.toolkt.reactive.event_stream

import dev.toolkt.core.collections.forEach
import dev.toolkt.core.collections.insertEffectively
import dev.toolkt.core.collections.insertEffectivelyWeak
import dev.toolkt.core.collections.mutableWeakMultiValuedMapOf
import dev.toolkt.core.collections.removeEffectively
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

abstract class ManagedEventStream<out EventT> : ProperEventStream<EventT>() {
    enum class State {
        Paused, Resumed,
    }

    // The order of listeners invocation is non-deterministic (this could be
    // changed by using a linked mutable set, but that wouldn't give much
    // without reworking the weak listeners too)
    private val listeners = mutableSetOf<Listener<EventT>>()

    // The order of weak listeners invocation is non-deterministic (changing
    // this would require a new multivalued map implementation)
    private val weakListeners = mutableWeakMultiValuedMapOf<Any, TargetingListener<Any, EventT>>()

    final override fun listen(
        listener: Listener<EventT>,
    ): Subscription {
        val remover = listeners.insertEffectively(listener)

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                remover.removeEffectively()

                potentiallyPause()
            }
        }
    }

    final override fun <TargetT : Any> listenWeak(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Subscription {
        val remover = weakListeners.insertEffectivelyWeak(
            key = target,
            value = @Suppress("UNCHECKED_CAST") (listener as TargetingListener<Any, EventT>),
        )

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                // We don't check whether the entry was successfully removed,
                // as the entry might've been purged if the target was collected
                remover.remove()
            }
        }
    }

    private val listenerCount: Int
        get() = listeners.size + weakListeners.size

    protected val state: State
        get() = when {
            listenerCount > 0 -> State.Resumed
            else -> State.Paused
        }

    private fun potentiallyResume() {
        if (listenerCount == 1) {
            onResumed()
        }
    }

    private fun potentiallyPause() {
        if (listenerCount == 0) {
            onPaused()
        }
    }

    protected fun notify(
        event: @UnsafeVariance EventT,
    ) {
        listeners.forEach {
            it(event)
        }

        if (weakListeners.isEmpty()) {
            return
        }

        weakListeners.forEach { (target, weakListener) ->
            weakListener(target, event)
        }

        // Iterating over the weak map may trigger unreachable entry purging,
        // the listener count may have reached zero
        potentiallyPause()
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}

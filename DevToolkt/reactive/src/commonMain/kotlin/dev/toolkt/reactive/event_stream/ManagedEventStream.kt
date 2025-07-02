package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription


abstract class ManagedEventStream<out EventT> : ProperEventStream<EventT>() {
    enum class State {
        Paused, Resumed,
    }

    private val strongListenerContainer = StrongListenerContainer<EventT>()

    private val weakListenerContainer = WeakListenerContainer<EventT>()

    final override fun listen(
        listener: Listener<EventT>,
    ): Subscription {
        val handle = strongListenerContainer.insert(
            listener = listener,
        )

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                handle.remove()

                potentiallyPause()
            }
        }
    }

    final override fun <TargetT : Any> listenWeak(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Subscription {
        val handle = weakListenerContainer.insertTargeted(
            target = target,
            listener = listener,
        )

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                handle.remove()
            }
        }
    }

    private val listenerCount: Int
        get() = strongListenerContainer.listenerCount + weakListenerContainer.listenerCount

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
        strongListenerContainer.notifyAll(event)

        if (weakListenerContainer.listenerCount > 0) {
            weakListenerContainer.notifyAll(event)

            // Iterating over the weak map may trigger unreachable entry purging,
            // the listener count may have reached zero
            potentiallyPause()
        }
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}

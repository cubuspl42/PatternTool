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

                potentiallyPause()
            }
        }
    }

    fun <TargetT : Any> pinWeak(
        target: TargetT,
    ): Subscription = listenWeak(target) { _, _ ->
        // The target and the actual event are not used when pinning. The pinning
        // mechanism could be potentially improved not to store the extraneous
        // lambda at all, but this is not a very big deal.
    }

    protected val listenerCount: Int
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

        weakListenerContainer.notifyAll(event)
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}

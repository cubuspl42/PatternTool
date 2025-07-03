package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription


abstract class ManagedEventStream<out EventT> : ProperEventStream<EventT>() {
    enum class State {
        Paused, Resumed, Aborted,
    }

    private val strongListenerContainer = StrongListenerContainer<EventT>()

    private val weakListenerContainer = WeakListenerContainer<EventT>()

    final override fun listen(
        listener: Listener<EventT>,
    ): Subscription {
        if (state == State.Aborted) {
            return Subscription.Noop
        }

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
        if (state == State.Aborted) {
            return Subscription.Noop
        }

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

    private var state: State = State.Paused

    private fun potentiallyResume() {
        if (state == State.Paused) {
            state = State.Resumed

            onResumed()
        }
    }

    private fun potentiallyPause() {
        if (state == State.Resumed) {
            state = State.Paused

            onPaused()
        }
    }

    protected fun notify(
        event: @UnsafeVariance EventT,
    ) {
        strongListenerContainer.notifyAll(event)

        weakListenerContainer.notifyAll(event)
    }

    protected fun abort() {
        if (state == State.Aborted) {
            throw IllegalStateException("The event stream is already aborted")
        }

        if (state == State.Resumed) {
            strongListenerContainer.clear()

            weakListenerContainer.clear()

            onPaused()
        }

        state = State.Aborted
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}

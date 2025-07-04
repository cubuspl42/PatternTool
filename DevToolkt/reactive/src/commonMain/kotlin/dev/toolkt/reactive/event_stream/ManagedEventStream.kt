package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

abstract class ManagedEventStream<EventT> : ProperEventStream<EventT>() {
    enum class State {
        Paused, Resumed, Aborted,
    }

    private val strongListenerContainer = StrongListenerContainer<EventT>()

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

    protected val listenerCount: Int
        get() = strongListenerContainer.listenerCount

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
        event: EventT,
    ) {
        strongListenerContainer.notifyAll(event)
    }

    protected fun abort() {
        if (state == State.Aborted) {
            throw IllegalStateException("The event stream is already aborted")
        }

        if (state == State.Resumed) {
            strongListenerContainer.clear()

            onPaused()
        }

        onAborted()

        state = State.Aborted
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()

    protected abstract fun onAborted()
}

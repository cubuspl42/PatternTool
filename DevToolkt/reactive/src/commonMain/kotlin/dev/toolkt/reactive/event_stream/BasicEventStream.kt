package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.platformNativeSetOf
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction

abstract class BasicEventStream<EventT> : ProperEventStream<EventT>() {
    enum class State {
        Paused, Resumed, Aborted,
    }

    private val listeners = platformNativeSetOf<Listener<EventT>>()

    final override fun listen(
        listener: Listener<EventT>,
    ): Subscription {
        if (state == State.Aborted) {
            return Subscription.Noop
        }

        addListener(
            listener = listener,
        )

        return object : Subscription {
            override fun cancel() {
                removeListener(listener = listener)
            }
        }
    }

    private fun addListener(
        listener: Listener<EventT>,
    ) {
        val wasAdded = listeners.add(listener)

        if (!wasAdded) {
            throw IllegalStateException("Listener is already registered")
        }

        potentiallyResume()
    }

    private fun removeListener(
        listener: Listener<EventT>,
    ) {
        // Don't check whether the removal was successful, as in a
        // corner case this stream might've already aborted. Currently,
        // the dependents aren't notified about its dependency stream
        // aborting, so they'll keep subscribing/unsubscribing as normal.
        listeners.remove(listener)

        potentiallyPause()
    }

    protected val listenerCount: Int
        get() = listeners.size

    private var state: State = State.Paused

    private fun potentiallyResume() {
        if (state == State.Paused) {
            state = State.Resumed

            onResumed()
        }
    }

    private fun potentiallyPause() {
        if (listenerCount == 0 && state == State.Resumed) {
            state = State.Paused

            onPaused()
        }
    }

    protected fun notify(
        transaction: Transaction,
        event: EventT,
    ) {
        // Create a snapshot of the listeners, as in consequence of the event
        // being propagated, new listeners might be added, or existing ones
        // removed. This way, we ensure that all listeners that were present
        // at the time of the event being emitted will receive it.

        // TODO: Make this logic transaction-aware (new listeners shouldn't literally be added during the transaction?)
        // Try designing a test for this ^

        val listeners = listeners.copy()

        listeners.forEach { listener ->
            val conclusion = listener.handle(
                transaction = transaction,
                event = event,
            )

            when (conclusion) {
                Listener.Conclusion.KeepListening -> {
                    // Keep listening, do nothing for now
                }

                Listener.Conclusion.StopListening -> {
                    // Remove the listener later
                    transaction.enqueueMutation {
                        removeListener(listener = listener)
                    }
                }
            }
        }
    }

    protected fun abort() {
        if (state == State.Aborted) {
            throw IllegalStateException("The event stream is already aborted")
        }

        listeners.clear()

        onAborted()

        state = State.Aborted
    }

    protected fun forwardFrom(
        source: EventStream<EventT>,
    ): Subscription = source.listen(
        listener = object : UnconditionalListener<EventT>() {
            override fun handleUnconditionally(
                transaction: Transaction,
                event: EventT,
            ) {
                notify(
                    transaction = transaction,
                    event = event,
                )
            }
        },
    )

    protected val controller: Controller<EventT>
        get() = object : Controller<EventT> {
            override fun accept(event: EventT) {
                Transaction.executeAll { transaction ->
                    notify(
                        transaction = transaction,
                        event = event,
                    )
                }
            }
        }

    protected abstract fun onResumed()

    protected abstract fun onPaused()

    protected abstract fun onAborted()
}

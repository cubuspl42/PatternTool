package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.core.platform.PlatformNativeSet
import dev.toolkt.core.platform.platformNativeSetOf
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream

abstract class EventStreamVertex<EventT> {
    enum class State {
        Paused, Resumed,
    }

    private var state: State = State.Paused

    private val listeners: PlatformNativeSet<Listener<EventT>> by lazy { platformNativeSetOf<Listener<EventT>>() }

    val listenerCount: Int
        get() = listeners.size

    /**
     * Adds a listener. If the listener is already registered, an exception is thrown.
     *
     * This method should be called during the mutation phase.
     */
    fun addListener(
        listener: Listener<EventT>,
    ) {
        val wasAdded = listeners.add(listener)

        if (!wasAdded) {
            throw IllegalStateException("Listener is already registered")
        }

        potentiallyResume()
    }

    /**
     * Removes a listener. If the listener is not registered, nothing happens.
     *
     * This method should be called during the mutation phase.
     */
    fun removeListener(
        listener: Listener<EventT>,
    ) {
        // Don't check whether the removal was successful, as in a
        // corner case this stream might've already aborted. Currently,
        // the dependents aren't notified about its dependency stream
        // aborting, so they'll keep subscribing/unsubscribing as normal.
        listeners.remove(listener)

        potentiallyPause()
    }

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

    /**
     * Notifies all listeners of an event.
     *
     * This method should be called during the propagation phase.
     */
    protected fun notify(
        transaction: Transaction,
        event: EventT,
    ) {
        listeners.forEach { listener ->
            val conclusion = listener.handle(
                transaction = transaction,
                event = event,
            )

            when (conclusion) {
                Listener.Conclusion.KeepListening -> {
                    // Keep listening, do nothing with the listener
                }

                Listener.Conclusion.StopListening -> {
                    // Remove the listener during the mutation phase
                    transaction.enqueueMutation {
                        removeListener(listener = listener)
                    }
                }
            }
        }
    }

    protected val controller: EventStream.Controller<EventT>
        get() = object : EventStream.Controller<EventT> {
            override fun accept(event: EventT) {
                Transaction.executeAll { transaction ->
                    notify(
                        transaction = transaction,
                        event = event,
                    )
                }
            }
        }

    /**
     * Called when the first listener is added.
     *
     * This method is called during the mutation phase.
     */
    protected abstract fun onResumed()

    /**
     * Called when the last listener is removed.
     *
     * This method is called during the mutation phase.
     */
    protected abstract fun onPaused()
}

/**
 * Adds a listener immediately.
 *
 * @return A subscription that can be used to remove the listener.
 *
 * This method should be called during the mutation phase.
 */
fun <EventT> EventStreamVertex<EventT>.subscribeNow(
    listener: Listener<EventT>,
): Subscription {
    addListener(
        listener = listener,
    )

    return object : Subscription {
        override fun cancel() {
            removeListener(listener = listener)
        }
    }
}

/**
 * Adds a listener in the mutation phase.
 *
 * @return A subscription that can be used to remove the listener.
 *
 * This method should be called during the propagation phase.
 */
fun <EventT> EventStreamVertex<EventT>.subscribeLater(
    transaction: Transaction,
    listener: Listener<EventT>,
): Subscription {
    transaction.enqueueMutation {
        addListener(
            listener = listener,
        )
    }

    return object : Subscription {
        override fun cancel() {
            removeListener(listener = listener)
        }
    }
}

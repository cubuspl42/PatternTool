package diy.lingerie.frp

import diy.lingerie.utils.iterable.forEachRemoving

abstract class NotifyingStream<E> : EventStream<E>() {
    private val eventListeners = mutableSetOf<Notifier.ListenerReference<E>>()

    final override fun subscribe(
        listener: Listener<E>,
        strength: Notifier.ListenerStrength,
    ): Subscription {
        val listenerReference = strength.refer(listener)

        val wasAdded = eventListeners.add(listenerReference)

        if (!wasAdded) {
            throw AssertionError("Listener reference was already present (???)")
        }

        if (eventListeners.size == 1) {
            onResumed()
        }

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = eventListeners.remove(listenerReference)

                if (!wasRemoved) {
                    throw IllegalStateException("Listener was not found")
                }

                if (eventListeners.isEmpty()) {
                    onPaused()
                }
            }
        }
    }

    protected fun notify(
        event: E,
    ) {
        eventListeners.forEachRemoving { listenerReference ->
            listenerReference.handle(event)
        }
    }

    protected open fun onResumed() {}

    protected open fun onPaused() {}
}

package diy.lingerie.frp

import diy.lingerie.utils.iterable.append
import diy.lingerie.utils.iterable.forEachRemoving

abstract class Vertex<T>() {
    private val listeners = mutableListOf<Notifier.ListenerReference<T>>()

    fun notify(
        value: T,
    ) {
        listeners.forEachRemoving { listenerReference ->
            listenerReference.handle(value)
        }
    }

    fun subscribe(
        listener: Listener<T>,
        strength: Notifier.ListenerStrength = Notifier.ListenerStrength.Strong,
    ): Subscription {
        val listenerReference = strength.refer(listener)

        val listenerIndex = listeners.append(listenerReference)

        if (listeners.size == 1) {
            onResumed()
        }

        return object : Subscription {
            override fun cancel() {
                listeners.removeAt(listenerIndex)

                if (listeners.isEmpty()) {
                    onPaused()
                }
            }

            override fun change(
                strength: Notifier.ListenerStrength,
            ) {
                val listenerReference = listeners.getOrNull(listenerIndex)
                    ?: throw AssertionError("Listener with index $listenerIndex not found (???)")

                val strongListener = listenerReference as? Notifier.ListenerReference.Strong<T> ?: throw AssertionError(
                    "Listener reference is weak (???)"
                )

                val listener = strongListener.listener

                val newReference = strength.refer(listener)

                listeners[listenerIndex] = newReference
            }
        }
    }

    protected open fun onResumed() {}

    protected open fun onPaused() {}
}

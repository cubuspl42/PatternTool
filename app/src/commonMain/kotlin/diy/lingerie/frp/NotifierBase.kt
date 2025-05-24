package diy.lingerie.frp

import diy.lingerie.utils.iterable.append
import diy.lingerie.utils.iterable.forEachRemoving

class NotifierBase<T> : Notifier<T> {
    private val listeners = mutableListOf<Notifier.ListenerReference<T>>()

    fun notify(
        value: T,
    ) {
        listeners.forEachRemoving { listenerReference ->
            listenerReference.handle(value)
        }
    }

    override fun subscribe(
        listener: Listener<T>,
        strength: Notifier.ListenerStrength,
    ): Subscription {
        val listenerReference = strength.refer(listener)

        val listenerIndex = listeners.append(listenerReference)

        return object : Subscription {
            override fun cancel() {
                listeners.removeAt(listenerIndex)
            }
        }
    }
}

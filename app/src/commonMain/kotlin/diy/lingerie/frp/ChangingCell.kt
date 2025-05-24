package diy.lingerie.frp

import diy.lingerie.frp.Notifier.ListenerStrength
import diy.lingerie.utils.iterable.forEachRemoving

abstract class ChangingCell<V>(
    initialValue: V,
) : Cell<V>() {
    private val changeListeners = mutableSetOf<Notifier.ListenerReference<Change<V>>>()

    private var mutableValue: V = initialValue

    final override val currentValue: V
        get() = mutableValue

    final override val changes: EventStream<Change<V>>
        get() = ProxyEventStream(source = this)

    protected fun update(newValue: V) {
        val oldValue = mutableValue

        val change = Change(
            oldValue = oldValue,
            newValue = newValue,
        )

        mutableValue = newValue

        changeListeners.forEachRemoving { listenerReference ->
            listenerReference.handle(change)
        }
    }

    override fun subscribe(
        listener: Listener<Change<V>>,
        strength: ListenerStrength,
    ): Subscription {
        val listenerReference = strength.refer(listener)

        val wasAdded = changeListeners.add(listenerReference)

        if (!wasAdded) {
            throw AssertionError("Value listener reference was already present (???)")
        }

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = changeListeners.remove(listenerReference)

                if (!wasRemoved) {
                    throw IllegalStateException("New value listener was not found")
                }
            }
        }
    }
}

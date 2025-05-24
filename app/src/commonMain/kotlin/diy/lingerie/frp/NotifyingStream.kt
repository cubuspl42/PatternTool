package diy.lingerie.frp

import diy.lingerie.utils.iterable.forEachRemoving

abstract class NotifyingStream<E> : EventStream<E>() {
    sealed class ListenerStrength {
        data object Weak : ListenerStrength() {
            override fun <E> refer(
                listener: Listener<E>,
            ): ListenerReference<E> = ListenerReference.Weak(
                weakListenerReference = PlatformWeakReference(value = listener),
            )
        }

        data object Strong : ListenerStrength() {
            override fun <E> refer(
                listener: Listener<E>,
            ): ListenerReference<E> = ListenerReference.Strong(listener = listener)
        }

        abstract fun <E> refer(
            listener: Listener<E>,
        ): ListenerReference<E>
    }

    sealed class ListenerReference<E> {
        class Strong<E>(
            private val listener: Listener<E>,
        ) : ListenerReference<E>() {
            override fun handle(event: E): Boolean {
                listener.handle(event)

                return false
            }
        }

        class Weak<E>(
            val weakListenerReference: PlatformWeakReference<Listener<E>>,
        ) : ListenerReference<E>() {
            override fun handle(
                event: E,
            ): Boolean {
                val listener = weakListenerReference.get() ?: return true

                listener.handle(event)

                return false
            }
        }

        /**
         * Handles the event.
         *
         * @return `true` if the listener is unreachable and should be removed
         */
        abstract fun handle(
            event: E,
        ): Boolean
    }

    private val listeners = mutableSetOf<ListenerReference<E>>()

    final override fun subscribe(
        listener: Listener<E>,
        strength: ListenerStrength,
    ): Subscription {
        val listenerReference = strength.refer(listener)

        val wasAdded = listeners.add(listenerReference)

        if (!wasAdded) {
            throw AssertionError("Listener reference was already present (???)")
        }

        if (listeners.size == 1) {
            onResumed()
        }

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = listeners.remove(listenerReference)

                if (!wasRemoved) {
                    throw IllegalStateException("Listener was not found")
                }

                if (listeners.isEmpty()) {
                    onPaused()
                }
            }
        }
    }

    protected fun notify(
        event: E,
    ) {
        listeners.forEachRemoving { listenerReference ->
            listenerReference.handle(event)
        }
    }

    protected open fun onResumed() {}

    protected open fun onPaused() {}
}

package diy.lingerie.frp

import diy.lingerie.utils.iterable.append
import diy.lingerie.utils.iterable.forEachRemoving

abstract class Vertex<T>() {
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
            val listener: Listener<E>,
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
    
    private val listeners = mutableListOf<Vertex.ListenerReference<T>>()

    fun notify(
        value: T,
    ) {
        listeners.forEachRemoving { listenerReference ->
            listenerReference.handle(value)
        }
    }

    fun subscribe(
        listener: Listener<T>,
        strength: Vertex.ListenerStrength = Vertex.ListenerStrength.Strong,
    ): Subscription {
        val listenerReference = strength.refer(listener)

        val listenerIndex = listeners.append(listenerReference)

        if (listeners.size == 1) {
            onResumed()
        }

        return object : Subscription {
            override fun cancel() {
                // FIXME: This is wrong, the indices move!
                //  Solution: set of _classic_ listeners, set of weak listeners, subscribe, subscribeWeak +
                //  hybrid subscription on top of that that remembers current strength and can change modes

                // Or good-old addListener(listener) / removeListener(listener) / addWeakListener(listener) / removeWeakListener(listener)
                // + statically choose the right call in onPause / onResume
                // + helper weakenListener(listener) [strong -> weak] / strengthenListener(listener) [weak -> strong] ?
                // + PlatformWeakSet [of listeners] ? :)
                listeners.removeAt(listenerIndex)

                if (listeners.isEmpty()) {
                    onPaused()
                }
            }

            override fun change(
                strength: Vertex.ListenerStrength,
            ) {
                val listenerReference = listeners.getOrNull(listenerIndex)
                    ?: throw AssertionError("Listener with index $listenerIndex not found (???)")

                val strongListener = listenerReference as? Vertex.ListenerReference.Strong<T> ?: throw AssertionError(
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

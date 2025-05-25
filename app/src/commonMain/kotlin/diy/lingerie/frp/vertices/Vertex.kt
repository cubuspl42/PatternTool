package diy.lingerie.frp.vertices

import diy.lingerie.frp.HybridSubscription
import diy.lingerie.frp.Listener
import diy.lingerie.frp.PlatformWeakReference
import diy.lingerie.frp.Subscription
import diy.lingerie.frp.mutableWeakSetOf

/**
 * A vertex in the functional-reactive dependency graph. Allows abstracting the
 * implementation details from the public interface and sharing common behavior
 * between cells, event stream and dynamic collections internals, though from
 * the public interface perspective these entities are strongly distinct.
 */
abstract class Vertex<T>() {
    enum class State {
        Paused, Resumed,
    }

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

    private var state = State.Paused

    private val strongListeners = mutableSetOf<Listener<T>>()

    private val weakListeners = mutableWeakSetOf<Listener<T>>()

    private fun hasListeners() = strongListeners.isNotEmpty() || weakListeners.isNotEmpty()

    fun notify(
        value: T,
    ) {
        strongListeners.forEach {
            it.handle(value)
        }

        weakListeners.forEach {
            it.handle(value)
        }

        // Touching the weak listeners set could purge all (unreachable) listeners
        pauseIfLostListeners()
    }

    fun subscribeStrong(
        listener: Listener<T>,
    ): Subscription {
        val wasAdded = strongListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already strongly-subscribed (???)")

        resumeIfPaused()

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = strongListeners.remove(listener)

                if (!wasRemoved) throw AssertionError("Listener is not strongly-subscribed (???)")

                pauseIfLostListeners()
            }
        }
    }

    fun subscribeWeak(
        listener: Listener<T>,
    ): Subscription {
        val wasAdded = weakListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already weakly-subscribed (???)")

        resumeIfPaused()

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = weakListeners.remove(listener)

                if (!wasRemoved) throw AssertionError("Listener is not weakly-subscribed (???)")

                pauseIfLostListeners()
            }
        }
    }

    /**
     * Subscribes a listener that can switch between strong and weak. The initial
     * subscription is weak.
     */
    fun subscribeHybrid(
        listener: Listener<T>,
    ): HybridSubscription = object : HybridSubscription {
        private var currentSubscription = subscribeWeak(listener = listener)

        override fun weaken() {
            currentSubscription.cancel()
            currentSubscription = subscribeWeak(listener = listener)
        }

        override fun strengthen() {
            currentSubscription.cancel()
            currentSubscription = subscribeStrong(listener = listener)
        }

        override fun cancel() {
            currentSubscription.cancel()
        }
    }

    private fun resumeIfPaused() {
        if (state == State.Paused) {
            onResumed()

            state = State.Resumed
        }
    }

    private fun pauseIfLostListeners() {
        if (!hasListeners() && state == State.Resumed) {
            onPaused()

            state = State.Paused
        }
    }

    protected open fun onResumed() {}

    protected open fun onPaused() {}
}

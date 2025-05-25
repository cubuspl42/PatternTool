package diy.lingerie.frp.vertices

import diy.lingerie.frp.HybridSubscription
import diy.lingerie.frp.Listener
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
            override fun <T> addListener(
                vertex: Vertex<T>, listener: Listener<T>
            ) {
                vertex.addStrongListener(listener = listener)
            }

            override fun <T> removeListener(
                vertex: Vertex<T>, listener: Listener<T>
            ) {
                vertex.removeStrongListener(listener = listener)
            }

        }

        data object Strong : ListenerStrength() {
            override fun <T> addListener(
                vertex: Vertex<T>, listener: Listener<T>
            ) {
                vertex.addWeakListener(listener = listener)
            }

            override fun <T> removeListener(
                vertex: Vertex<T>, listener: Listener<T>
            ) {
                vertex.removeWeakListener(listener = listener)
            }
        }

        abstract fun <T> addListener(
            vertex: Vertex<T>,
            listener: Listener<T>,
        )

        abstract fun <T> removeListener(
            vertex: Vertex<T>,
            listener: Listener<T>,
        )
    }

    companion object {
        private var nextId = 0
    }

    @Suppress("unused")
    private val id: Int = nextId++

    protected abstract val kind: String

    protected val name: String
        get() = "$kind#$id"

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
        pauseIfLostListeners(
            phase = "post-notify",
        )
    }

    /**
     * Add a strong listener to the vertex. This is a low-level operation.
     */
    internal fun addStrongListener(
        listener: Listener<T>,
    ) {
        val wasAdded = strongListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already strongly-subscribed (???)")

        resumeIfPaused(
            phase = "subscribe-strong",
        )
    }

    internal fun removeStrongListener(
        listener: Listener<T>,
    ) {
        val wasRemoved = strongListeners.remove(listener)

        if (!wasRemoved) throw AssertionError("Listener is not strongly-subscribed (???)")

        pauseIfLostListeners(
            phase = "post-strong-cancel",
        )
    }

    /**
     * Add a weak listener to the vertex. This is a low-level operation.
     */
    internal fun addWeakListener(
        listener: Listener<T>,
    ) {
        val wasAdded = weakListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already weakly-subscribed (???)")

        resumeIfPaused(
            phase = "subscribe-weak",
        )
    }

    internal fun removeWeakListener(
        listener: Listener<T>,
    ) {
        val wasRemoved = weakListeners.remove(listener)

        if (!wasRemoved) throw AssertionError("Listener is not weakly-subscribed (???)")

        pauseIfLostListeners(
            phase = "post-weak-cancel",
        )
    }


    fun subscribeStrong(
        listener: Listener<T>,
    ): Subscription {
        val wasAdded = strongListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already strongly-subscribed (???)")

        resumeIfPaused(
            phase = "subscribe-strong",
        )

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = strongListeners.remove(listener)

                if (!wasRemoved) throw AssertionError("Listener is not strongly-subscribed (???)")

                pauseIfLostListeners(
                    phase = "post-strong-cancel",
                )
            }
        }
    }


    fun subscribeWeak(
        listener: Listener<T>,
    ): Subscription {
        val wasAdded = weakListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already weakly-subscribed (???)")

        resumeIfPaused(
            phase = "subscribe-weak",
        )

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = weakListeners.remove(listener)

                if (!wasRemoved) throw AssertionError("Listener is not weakly-subscribed (???)")

                pauseIfLostListeners(
                    phase = "post-weak-cancel",
                )
            }
        }
    }

    /**
     * Subscribes a listener that can switch between strong and weak. The initial
     * subscription is weak.
     */
    fun subscribeHybrid(
        listener: Listener<T>,
        initialStrength: ListenerStrength = ListenerStrength.Weak,
    ): HybridSubscription {
        initialStrength.addListener(
            vertex = this,
            listener = listener,
        )

        return object : HybridSubscription {
            private var currentStrength = initialStrength

            override fun cancel() {
                currentStrength.removeListener(
                    vertex = this@Vertex,
                    listener = listener,
                )
            }

            override fun updateStrength(
                newStrength: ListenerStrength,
            ) {
                if (currentStrength == newStrength) return

                currentStrength.removeListener(
                    vertex = this@Vertex,
                    listener = listener,
                )

                newStrength.addListener(
                    vertex = this@Vertex,
                    listener = listener,
                )

                currentStrength = newStrength
            }
        }
    }

    private fun resumeIfPaused(
        phase: String,
    ) {
        if (state == State.Paused) {
            println("Resuming vertex $name [$phase]...")

            onResumed()

            state = State.Resumed
        }
    }

    private fun pauseIfLostListeners(
        phase: String,
    ) {
        if (!hasListeners() && state == State.Resumed) {
            println("Pausing vertex $name [$phase]...")

            onPaused()

            state = State.Paused
        }
    }

    protected open fun onResumed() {}

    protected open fun onPaused() {}
}

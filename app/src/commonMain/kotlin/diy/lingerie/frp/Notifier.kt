package diy.lingerie.frp

import diy.lingerie.frp.Notifier.ListenerStrength

private val finalizationRegistry = PlatformFinalizationRegistry()

interface Notifier<out T> {
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

    fun subscribe(
        listener: Listener<T>,
        strength: ListenerStrength = ListenerStrength.Strong,
    ): Subscription
}

fun <E> Notifier<E>.subscribeFullyBound(
    target: Any,
    listener: Listener<E>,
) {
    // Ignore the cleanable, depend on the finalization register only

    subscribeBound(
        target = target,
        listener = listener,
    )
}

private fun <E> Notifier<E>.subscribeBound(
    target: Any,
    listener: Listener<E>,
): PlatformCleanable {
    val weakSubscription = subscribe(
        listener = listener,
        strength = ListenerStrength.Weak,
    )

    return finalizationRegistry.register(
        target = target,
    ) {
        weakSubscription.cancel()
    }
}

fun <E> Notifier<E>.subscribeSemiBound(
    target: Any,
    listener: Listener<E>,
): Subscription {
    val cleanable = subscribeBound(
        target,
        listener,
    )

    return object : Subscription {
        override fun cancel() {
            cleanable.clean()
        }
    }
}

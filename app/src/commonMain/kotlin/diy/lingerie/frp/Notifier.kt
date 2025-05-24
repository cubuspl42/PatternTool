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

    fun subscribe(
        listener: Listener<T>,
        strength: ListenerStrength = ListenerStrength.Strong,
    ): Subscription
}

fun <T> Vertex<T>.subscribeFullyBound(
    target: Any,
    listener: Listener<T>,
) {
    // Ignore the cleanable, depend on the finalization register only

    subscribeBound(
        target = target,
        listener = listener,
    )
}

private fun <T> Vertex<T>.subscribeBound(
    target: Any,
    listener: Listener<T>,
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

//fun <T> Vertex<T>.subscribeSemiBound(
//    target: Any,
//    listener: Listener<T>,
//): Subscription {
//    val cleanable = subscribeBound(
//        target,
//        listener,
//    )
//
//    return object : Subscription {
//        override fun cancel() {
//            cleanable.clean()
//        }
//
//        override fun change(strength: ListenerStrength) {
//            TODO("Not yet implemented")
//        }
//    }
//}

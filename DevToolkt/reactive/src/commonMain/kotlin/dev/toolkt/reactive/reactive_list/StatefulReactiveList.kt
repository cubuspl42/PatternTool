package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.listen
import dev.toolkt.reactive.event_stream.pinWeak

abstract class StatefulReactiveList<E>(
    initialElements: List<E>,
    givenChanges: EventStream<Change<E>>,
) : ActiveReactiveList<E>() {
    private val storedElements: MutableList<E> = initialElements.toMutableList()

    override val currentElements: List<E>
        get() = storedElements.toList()

    override val changes: EventStream<Change<E>> = object : DependentEventStream<Change<E>>() {
        private val weakReactiveList = PlatformWeakReference(this@StatefulReactiveList)

        private val self = this // Kotlin doesn't ofer a label for `this@DependentEventStream` (why?)

        override fun observe(): Subscription = givenChanges.listen { change ->
            // Notify the listeners about the change...
            self.notify(event = change)

            // ...and apply it (only if needed)
            val reactiveList = weakReactiveList.get() ?: return@listen

            change.applyTo(reactiveList.storedElements)
        }

        init {
            // Pin the event stream to keep the stored elements up-to-date even when there are no listeners
            self.pinWeak(target = this@StatefulReactiveList)
        }
    }
}

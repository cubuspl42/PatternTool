package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.VertexEventStream
import dev.toolkt.reactive.event_stream.pinWeak
import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.PassiveEventStreamVertex

abstract class StatefulReactiveList<E>(
    initialElements: List<E>,
    givenChanges: EventStream<Change<E>>,
) : ProperReactiveList<E>() {
    private val storedElements: MutableList<E> = initialElements.toMutableList()

    override val currentElementsUnmanaged: List<E>
        get() = storedElements.toList()

    override val changes: EventStream<Change<E>> = object : VertexEventStream<Change<E>>() {
        private val weakReactiveList = PlatformWeakReference(this@StatefulReactiveList)

        private val self = this // Kotlin doesn't ofer a label for `this@PassiveEventStream` (why?)

        // FIXME: Vertex should be lazy-initialized; this whole thing should be ported to a RL vertex
        override val vertex: EventStreamVertex<Change<E>> = object : PassiveEventStreamVertex<Change<E>>() {
            override fun observe(): Subscription = givenChanges.listen(
                listener = object : UnconditionalListener<Change<E>>() {
                    override val dependentId = id

                    override fun handleUnconditionally(
                        transaction: dev.toolkt.reactive.effect.Transaction,
                        event: Change<E>,
                    ) {
                        // Notify the listeners about the change...
                        self.notify(
                            transaction = transaction,
                            event = event,
                        )

                        // ...and apply it (only if needed)
                        val reactiveList = weakReactiveList.get() ?: return

                        event.applyTo(reactiveList.storedElements)
                    }
                },
            )
        }

        init {
            // Pin the event stream to keep the stored elements up-to-date even when there are no listeners
            self.pinWeak(target = this@StatefulReactiveList)
        }
    }
}

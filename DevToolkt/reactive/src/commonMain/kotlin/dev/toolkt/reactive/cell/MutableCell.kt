package dev.toolkt.reactive.cell

import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.VertexEventStream
import dev.toolkt.reactive.event_stream.vertex.EmitterEventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex

class MutableCell<V> private constructor(
    private val newValueEmitterVertex: EmitterEventStreamVertex<V>,
    initialValue: V,
) : ProperCell<V>() {
    class NewValuesEventStream<V>(
        override val vertex: EventStreamVertex<V>,
    ) : VertexEventStream<V>()

    companion object {
        /**
         * Creates a new [MutableCell] with the given [initialValue].
         *
         * [MomentContext] is needed only to give the mutable cell its identity.
         */
        context(momentContext: MomentContext) fun <V> create(
            initialValue: V,
        ): MutableCell<V> = MutableCell(
            newValueEmitterVertex = EmitterEventStreamVertex(),
            initialValue = initialValue,
        )
    }

    private var mutableValue: V = initialValue

    val hasListeners: Boolean
        get() = newValueEmitterVertex.listenerCount > 0

    override val newValues: EventStream<V>
        get() = NewValuesEventStream(vertex = newValueEmitterVertex)

    context(momentContext: MomentContext) override fun sample(): V = mutableValue

    override val currentValueUnmanaged: V
        get() = mutableValue

    context(actionContext: ActionContext) fun set(
        newValue: V,
    ) {
        actionContext.transaction.enqueueFollowup { followupTransaction ->
            newValueEmitterVertex.emit(
                transaction = followupTransaction,
                event = newValue,
            )

            followupTransaction.enqueueMutation {
                mutableValue = newValue
            }
        }
    }
}

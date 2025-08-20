package dev.toolkt.reactive.cell

import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

class MutableCell<V> private constructor(
    private val newValueEmitter: EventEmitter<V>,
    initialValue: V,
) : ProperCell<V>() {
    companion object {
        @Deprecated("Use `create` instead")
        fun <ValueT> createUnmanaged(
            initialValue: ValueT,
        ): MutableCell<ValueT> = MutableCell(
            newValueEmitter = EventEmitter.createUnmanaged(),
            initialValue = initialValue,
        )

        /**
         * Creates a new [MutableCell] with the given [initialValue].
         *
         * [MomentContext] is needed only to give the mutable cell its identity.
         */
        context(momentContext: MomentContext) fun <V> create(
            initialValue: V,
        ): MutableCell<V> {
            return MutableCell(
                newValueEmitter = EventEmitter.create(),
                initialValue = initialValue,
            )
        }
    }


    private var mutableValue: V = initialValue

    val hasListeners: Boolean
        get() = newValueEmitter.hasListeners

    override val newValues: EventStream<V>
        get() = newValueEmitter

    context(momentContext: MomentContext) override fun sample(): V = mutableValue

    override val currentValueUnmanaged: V
        get() = mutableValue

    context(actionContext: ActionContext) fun set(
        newValue: V,
    ) {
        actionContext.enqueueMutation {
            mutableValue = newValue
        }

        newValueEmitter.emit(newValue)
    }
}
